package cn.leyou.cart.interceptor;


import cn.leyou.auth.pojo.UserInfo;
import cn.leyou.auth.utils.JwtUtils;
import cn.leyou.auth.utils.RsaUtils;
import cn.leyou.cart.config.JwtProperties;
import cn.leyou.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@EnableConfigurationProperties(JwtProperties.class)
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> t1 = new ThreadLocal<UserInfo>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String cookieValue = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        if (StringUtils.isBlank(cookieValue)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        try {
            UserInfo infoFromToken = JwtUtils.getInfoFromToken(cookieValue, jwtProperties.getPublicKey());
            t1.set(infoFromToken);
            return true;
        } catch (Exception e) {
            // 抛出异常，证明未登录,返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        t1.remove();
    }

    public static UserInfo getUserInfo(){
        return t1.get();
    }
}
