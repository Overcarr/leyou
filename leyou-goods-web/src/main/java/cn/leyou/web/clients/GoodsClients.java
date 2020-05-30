package cn.leyou.web.clients;


import cn.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient("item-service")
@Component
public interface GoodsClients extends GoodsApi {
}
