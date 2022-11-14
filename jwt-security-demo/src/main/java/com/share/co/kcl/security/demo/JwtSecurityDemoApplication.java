package com.share.co.kcl.security.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class JwtSecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtSecurityDemoApplication.class, args);
    }

}
