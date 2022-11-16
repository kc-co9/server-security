package com.share.co.kcl.security.demo.deprecated;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class JwtSecurityDemoDeprecatedApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtSecurityDemoDeprecatedApplication.class, args);
    }

}
