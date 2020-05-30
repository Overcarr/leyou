package cn.leyou.search.clients;

import cn.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient("item-service")
@Component
public interface BrandClient extends BrandApi {
}
