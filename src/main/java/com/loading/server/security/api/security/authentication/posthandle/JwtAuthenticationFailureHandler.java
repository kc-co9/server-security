package com.loading.server.security.api.security.authentication.posthandle;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败会调用此方法，此处必须重写。因为默认会进行重定向到html页面的
 * <p>
 * <a href=https://docs.spring.io/spring-security/site/docs/5.5.2-SNAPSHOT/reference/html5/#servlet-authentication-abstractprocessingfilter>详情可阅读</a>
 *
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // TODO 待处理
    }
}
