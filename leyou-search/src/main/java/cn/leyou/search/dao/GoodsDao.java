package cn.leyou.search.dao;

import cn.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsDao extends ElasticsearchRepository<Goods,Long> {
}
