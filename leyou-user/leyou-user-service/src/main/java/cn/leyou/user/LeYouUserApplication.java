package cn.leyou.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("cn.leyou.user.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class LeYouUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeYouUserApplication.class);
    }
}
