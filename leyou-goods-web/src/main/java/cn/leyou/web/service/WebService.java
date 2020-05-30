package cn.leyou.web.service;

import cn.leyou.item.pojo.*;
import cn.leyou.web.clients.BrandClients;
import cn.leyou.web.clients.CategoryClients;
import cn.leyou.web.clients.GoodsClients;
import cn.leyou.web.clients.SpecClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebService {
    @Autowired
    private BrandClients brandClients;

    @Autowired
    private CategoryClients categoryClients;

    @Autowired
    private GoodsClients goodsClients;

    @Autowired
    private SpecClients specClients;

    public Map<String, Object> loadData(Long spuId){
        Map<String, Object> map = new HashMap<>();

        // 根据id查询spu对象
        Spu spu = this.goodsClients.selectSpuById(spuId);

        // 查询spudetail
        SpuDetail spuDetail = this.goodsClients.querySpuDeatils(spuId);

        // 查询sku集合
        List<Sku> skus = this.goodsClients.querySkus(spuId);

        // 查询分类
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClients.queryNamesByIds(cids);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id", cids.get(i));
            categoryMap.put("name", names.get(i));
            categories.add(categoryMap);
        }

        // 查询品牌
        Brand brand = this.brandClients.queryBrandById(spu.getBrandId());

        // 查询规格参数组
        List<SpecGroup> groups = this.specClients.querySpecGroups(spu.getCid3());

        // 查询特殊的规格参数
        List<SpecParam> params = this.specClients.querySpecParams(null, spu.getCid3(), false, null);
        Map<Long, String> paramMap = new HashMap<>();
        params.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });

        // 封装spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);
        // 封装sku集合
        map.put("skus", skus);
        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brand);
        // 规格参数组
        map.put("groups", groups);
        // 查询特殊规格参数
        map.put("paramMap", paramMap);
        return map;
    }
}
