package com.loading.server.security.api.security.authentication;

import com.loading.server.security.api.security.authentication.entity.JwtAuthentication;
import com.loading.server.security.api.security.authentication.posthandle.JwtAuthenticationFailureHandler;
import com.loading.server.security.api.security.authentication.posthandle.JwtAuthenticationSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.loading.server.security.api.security.utils.SecurityUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@code JwtAuthenticationProcessingFilter} is custom implements of {@code AbstractAuthenticationProcessingFilter}.
 * It is used to filter all request and extract {@code JwtAuthentication} from request.
 * more: <a href=https://docs.spring.io/spring-security/site/docs/5.5.2-SNAPSHOT/reference/html5/#servlet-authentication-abstractprocessingfilter></a>
 *
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final String TOKEN_KEY = "token";

    public JwtAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/**"));
        this.setAuthenticationManager(authenticationManager);
        // 此处必须设置为true，否则成功认证就会立刻终止，而不会继续走到路由请求
        // 同时设置为true也需要注意不能在successfulAuthentication设置Authentication，
        // 因为期间会去到AnonymousAuthenticationFilter先把token设置为AnonymousAuthenticationToken，从而导致授权判断失败
        this.setContinueChainBeforeSuccessfulAuthentication(true);
        // 此处两个处理器必须覆盖，否则默认会进行重定向到页面
        this.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        this.setAuthenticationFailureHandler(new JwtAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        String token = httpServletRequest.getHeader(TOKEN_KEY);
        String body = SecurityUtils.parseToken(token);
        JwtAuthentication authentication = new JwtAuthentication(body);
        return this.getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // Fire event
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

}
