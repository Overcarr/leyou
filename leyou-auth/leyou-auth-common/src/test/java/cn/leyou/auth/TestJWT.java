package cn.leyou.auth;

import cn.leyou.auth.pojo.UserInfo;
import cn.leyou.auth.utils.JwtUtils;
import cn.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;


public class TestJWT {

    private static final String pubKeyPath = "C:\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "C:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU5MDU4OTg3Mn0.V7Y30rPEvsKUJ9qFEsJMfMfxTLEa5aIFGA2zWKiTXWH5yE-l7MF4a-DGXLfg42rXSIY_UZZ8japSZRDLRbMvoIM042Nzv-sg3-jabCjJfPfGjiTRpmQCKUpOrUl9LQSqwpBb1Bm3AbyM5QJLq5aXCeH0K-N-soMNjFUh3oSjRK0";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
