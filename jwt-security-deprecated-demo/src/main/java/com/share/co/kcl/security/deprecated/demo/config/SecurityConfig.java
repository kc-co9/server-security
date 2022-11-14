package com.share.co.kcl.security.deprecated.demo.config;

import com.share.co.kcl.security.deprecated.demo.processor.filter.JwtAccessDeniedHandler;
import com.share.co.kcl.security.deprecated.demo.processor.filter.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

}
