package cn.leyou.web.clients;

import cn.leyou.item.api.SpecApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient("item-service")
@Component
public interface SpecClients extends SpecApi {
}
