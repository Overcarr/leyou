package cn.leyou.search.service;

import cn.leyou.item.pojo.*;
import cn.leyou.pojo.ResultType;
import cn.leyou.search.clients.BrandClient;
import cn.leyou.search.clients.CategoryClient;
import cn.leyou.search.clients.GoodsClient;
import cn.leyou.search.clients.SpecClient;
import cn.leyou.search.dao.GoodsDao;
import cn.leyou.search.pojo.Goods;
import cn.leyou.search.pojo.SearchRequest;
import cn.leyou.search.pojo.SearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.ml.GetCategoriesRequest;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class SearchService {


    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsDao goodsDao;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {

        // 创建goods对象
        Goods goods = new Goods();

        // 查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        // 查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        // 查询spu下的所有sku
        List<Sku> skus = this.goodsClient.querySkus(spu.getId());
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        // 遍历skus，获取价格集合
        skus.forEach(sku ->{
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });

        // 查询出所有的搜索规格参数
        List<SpecParam> params = this.specClient.querySpecParams(null, spu.getCid3(), null, true);
        // 查询spuDetail。获取规格参数值
        SpuDetail spuDetail = this.goodsClient.querySpuDeatils(spu.getId());
        // 获取通用的规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        // 获取特殊的规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });
        // 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()){
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });

        // 设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public ResultType<Goods> searchPage(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)){
            return null;
        }
        // 添加条件构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        /*MatchQueryBuilder builder = QueryBuilders.matchQuery("all", key).operator(Operator.AND);*/
        // bool查询
        BoolQueryBuilder boolQueryBuilder = boolQuerybuilder(searchRequest);
        // 添加bool查询语句
        queryBuilder.withQuery(boolQueryBuilder);
        // 添加要显示的字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        // 分页
        Integer page = searchRequest.getPage();
        Integer size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1,size));
        // 排序
        String sortBy = searchRequest.getSortBy();
        Boolean descending = searchRequest.getDescending();
        if (StringUtils.isNotBlank(sortBy)){
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending? SortOrder.DESC:SortOrder.ASC));
        }
        //聚合 分类及品牌
        String categories = "categories";
        String brands = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categories).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brands).field("brandId"));
        //查询
        AggregatedPage<Goods> goods = (AggregatedPage<Goods>)goodsDao.search(queryBuilder.build());
        // 解析品牌
        List<Brand> list1 = getBrandAgg(goods.getAggregation(brands));
        //解析分类
        List<Map<String,Object>> list2 = getCategoriesAgg(goods.getAggregation(categories));
        List<Map<String,Object>> specs = new ArrayList<>();
        // 参数
        if (list2.size()==1){
            specs =getSpecsAgg((Long) list2.get(0).get("id"),boolQueryBuilder);
        }
        return new SearchResult(goods.getContent(),goods.getTotalElements(),goods.getTotalPages(),list2,list1,specs);
    }

    /**
     * 创建bool查询语句
     * @param searchRequest
     * @return
     */
    private BoolQueryBuilder boolQuerybuilder(SearchRequest searchRequest) {
        // 创建bool查询语句
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 匹配查询 必须
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND));
        // 添加过滤条件
        if (searchRequest.getFilter()==null || searchRequest.getSize()==0){
            return boolQueryBuilder;
        }
        for (Map.Entry<String, String> entry : searchRequest.getFilter().entrySet()){
             String key = entry.getKey();
             if (key.equals("品牌")){
                 key = "brandId";
             } else if(key.equals("分类")){
                 key = "cid3";
             }else {
                 key = "specs."+key+".keyword";
             }
             boolQueryBuilder.filter(QueryBuilders.matchQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 解析规格参数聚合
     * @param id
     * @param builder
     * @return
     */
    private List<Map<String, Object>> getSpecsAgg(Long id, QueryBuilder builder) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(builder);
        List<SpecParam> specParams = specClient.querySpecParams(null, id, null, true);
        for (SpecParam specParam : specParams) {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
        }
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        AggregatedPage<Goods> goodsSearch = (AggregatedPage<Goods>)goodsDao.search(queryBuilder.build());

        List<Map<String,Object>> maps = new ArrayList<>();
        Map<String, Aggregation> aggregationMap = goodsSearch.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()){
            Map<String,Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put("k",entry.getKey());
            // 收集规格参数值
            List<Object> options = new ArrayList<>();
            StringTerms terms = (StringTerms)entry.getValue();
            terms.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            stringObjectMap.put("options",options);
            maps.add(stringObjectMap);
        }
        return maps;
    }


    // 品牌

    /**
     * 解析品牌聚合
     * @param aggregation
     * @return
     */
    List<Brand> getBrandAgg(Aggregation aggregation){
        // vaule值的类型
        LongTerms terms = (LongTerms)aggregation;
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        List<Brand> list = new ArrayList<>();
        for (LongTerms.Bucket bucket : buckets) {
            long l = bucket.getKeyAsNumber().longValue();
            Brand brand = this.brandClient.queryBrandById(l);
            list.add(brand);
        }
        return list;
    }

    // 分类

    /**
     * 解析分类聚合
     * @param aggregation
     * @return
     */
    List<Map<String,Object>> getCategoriesAgg(Aggregation aggregation){
        LongTerms terms = (LongTerms)aggregation;
        List<LongTerms.Bucket> buckets = ((LongTerms) aggregation).getBuckets();
        // 定义一个品牌集合，搜集所有的品牌对象
        List<Map<String, Object>> categories = new ArrayList<>();
        List<Long> cids = new ArrayList<>();
        for (LongTerms.Bucket bucket : buckets) {
            long cid = bucket.getKeyAsNumber().longValue();
            cids.add(cid);
        }
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }
        return categories;
    }

    public void createIndex(Long id) throws IOException {
        Spu spu = this.goodsClient.selectSpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsDao.save(goods);
    }


    public void delectIndex(Long id) throws IOException {

        this.goodsDao.deleteById(id);
    }
}

