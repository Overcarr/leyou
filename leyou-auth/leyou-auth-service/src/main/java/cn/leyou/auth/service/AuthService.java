package cn.leyou.auth.service;

import cn.leyou.auth.cliten.UserClient;
import cn.leyou.auth.configure.JwtProperties;
import cn.leyou.auth.pojo.UserInfo;
import cn.leyou.auth.utils.JwtUtils;
import cn.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProperties;

    public String authentication(String username, String password) throws Exception {

        User user = userClient.queryUser(username, password);
        if (user==null){
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        String token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        return token;


    }
}
