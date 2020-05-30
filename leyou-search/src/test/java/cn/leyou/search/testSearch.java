package cn.leyou.search;

import cn.leyou.item.pojo.Spu;
import cn.leyou.item.pojo.SpuVo;
import cn.leyou.pojo.ResultType;
import cn.leyou.search.clients.GoodsClient;
import cn.leyou.search.dao.GoodsDao;
import cn.leyou.search.pojo.Goods;
import cn.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchApplication.class)
public class testSearch {
    @Autowired
    public ElasticsearchTemplate template;
    @Autowired
    public GoodsDao goodsDao;
    @Autowired
    public GoodsClient goodsClient;
    @Autowired
    public SearchService searchService;

    @Test
    public void saveGoods(){
        this.template.createIndex(Goods.class);
        this.template.putMapping(Goods.class);

        Integer page = 1;
        Integer rows = 100;

        do{
            ResultType<SpuVo> spuVos = this.goodsClient.querySpuVoList(null, true, page, rows);
            List<Goods> collect = spuVos.getItems().stream().map(spuVo -> {
                try {
                    Goods goods = this.searchService.buildGoods((Spu) spuVo);
                    return goods;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
            Iterable<Goods> goods = goodsDao.saveAll(collect);
            rows = spuVos.getItems().size();
            page++;
        }while (rows == 100);

    }
}
