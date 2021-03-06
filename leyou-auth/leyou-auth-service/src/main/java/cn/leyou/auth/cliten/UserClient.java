package cn.leyou.auth.cliten;


import cn.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient("user-service")
@Component
public interface UserClient extends UserApi {
}
