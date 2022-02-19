package com.loading.server.security.api.security.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限校验失败会调用此方法
 * <p>
 * 详情可阅读<a href=https://docs.spring.io/spring-security/site/docs/5.5.2-SNAPSHOT/reference/html5/#servlet-authorization-filtersecurityinterceptor></a>
 * <p>
 * 注意，此处虽然有实现相应逻辑，但是过滤器在advice之后执行，所以在advice就被拦截了，即异常逻辑在advice中处理。
 * 详情可阅读{@link JwtExceptionHandler}
 *
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse httpServletResponse, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // TODO 处理权限不足的情况
    }
}
