package cn.leyou.auth.controller;


import cn.leyou.auth.configure.JwtProperties;
import cn.leyou.auth.pojo.UserInfo;
import cn.leyou.auth.service.AuthService;
import cn.leyou.auth.utils.JwtUtils;
import cn.leyou.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("accredit")
    public ResponseEntity<Void> authentication(@RequestParam("username")String username,
                                               @RequestParam("password")String password,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {

        String token = this.authService.authentication(username,password);
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getCookieMaxAge(),null,true);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN")String token) throws Exception {
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        if (userInfo==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userInfo);
    }

}
