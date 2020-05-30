package cn.leyou.user.api;


import cn.leyou.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("user")
public interface UserApi {

    @GetMapping("query")
    public User queryUser(@RequestParam("username")String username,
                                          @RequestParam("password")String password);
}