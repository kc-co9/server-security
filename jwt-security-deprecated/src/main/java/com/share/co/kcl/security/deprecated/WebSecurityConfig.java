package com.share.co.kcl.security.deprecated;

import com.share.co.kcl.security.deprecated.authentication.JwtAuthenticationProcessingFilter;
import com.share.co.kcl.security.deprecated.authentication.model.JwtAuthenticationToken;
import com.share.co.kcl.security.deprecated.authentication.manager.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"com.share.co.kcl.security"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final List<AuthenticationProvider> authenticationProviders;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public WebSecurityConfig(
            List<AuthenticationProvider> authenticationProviders,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler) {
        this.authenticationProviders = authenticationProviders;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        if (!CollectionUtils.isEmpty(this.authenticationProviders)) {
            for (AuthenticationProvider authenticationProvider : this.authenticationProviders) {
                auth.authenticationProvider(authenticationProvider);
            }
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 跨域
                .cors()
                .and()
                // CSRF
                .csrf().disable()
                // header
                .headers()
                .httpStrictTransportSecurity().disable()
                .frameOptions().disable()
                .and()
                // 配置 anonymous
                .anonymous()
                .principal(0)
                .and()
                .addFilterAt(new JwtAuthenticationProcessingFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                // 授权异常
                .exceptionHandling()
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .accessDeniedHandler(this.accessDeniedHandler)
                // 不创建会话
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 默认所有请求通过，在需要权限的方法加上安全注解
                .and()
                .authorizeRequests()
                .anyRequest().permitAll();
    }

    @Bean
    public static AuthenticationProvider jwtAuthenticationProvider(AuthenticationUserDetailsService<JwtAuthenticationToken> authenticationUserDetailsService) {
        return new JwtAuthenticationProvider(authenticationUserDetailsService);
    }

}
