package com.share.co.kcl.security;

import com.share.co.kcl.security.authentication.JwtPreAuthenticatedProcessingFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"com.share.co.kcl.security"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public WebSecurityConfig(
            AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler) {
        this.authenticationUserDetailsService = authenticationUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(this.authenticationUserDetailsService);
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider);
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
                .addFilter(new JwtPreAuthenticatedProcessingFilter(authenticationManager()))
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
                .anyRequest().permitAll()
        ;
    }
}
