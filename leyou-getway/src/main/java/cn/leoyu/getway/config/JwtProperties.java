package cn.leoyu.getway.config;

import cn.leyou.auth.utils.JwtUtils;
import cn.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "leyou.jwt")
@Data
public class JwtProperties {
    private String pubKeyPath;
    private PublicKey publicKey;
    private String cookieName;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtProperties.class);
    @PostConstruct
    public void init() throws Exception {
        try {
            PublicKey publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            LOGGER.info("初始化公钥失败");
            e.printStackTrace();
        }
    }

}
