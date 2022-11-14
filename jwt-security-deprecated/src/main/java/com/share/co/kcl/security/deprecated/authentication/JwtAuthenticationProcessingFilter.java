package com.share.co.kcl.security.deprecated.authentication;

import com.alibaba.fastjson.JSON;
import com.share.co.kcl.security.deprecated.authentication.model.JwtAuthenticationToken;
import com.share.co.kcl.security.common.constants.RequestParamsConstants;
import com.share.co.kcl.security.common.model.JwtObject;
import com.share.co.kcl.security.common.utils.SecurityUtils;
import com.share.co.kcl.security.deprecated.authentication.model.JwtAuthenticationTokenDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

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

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationProcessingFilter.class);

    public JwtAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/**"));
        this.setAuthenticationManager(authenticationManager);
        // 此处必须设置为true，否则成功认证就会立刻终止，而不会继续走到路由请求
        // 同时设置为true也需要注意不能在successfulAuthentication设置Authentication，
        // 因为期间会去到AnonymousAuthenticationFilter先把token设置为AnonymousAuthenticationToken，从而导致授权判断失败
        this.setContinueChainBeforeSuccessfulAuthentication(true);
        this.setAuthenticationDetailsSource(new JwtAuthenticationDetailsSource());
        // 此处两个处理器必须覆盖，否则默认会进行重定向到页面
        this.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        this.setAuthenticationFailureHandler(new JwtAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        String token = httpServletRequest.getHeader(RequestParamsConstants.TOKEN);
        String jwtString = SecurityUtils.parseToken(token);
        JwtObject jwtObject = JSON.parseObject(jwtString, JwtObject.class);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwtObject, token);
        jwtAuthenticationToken.setDetails(this.authenticationDetailsSource.buildDetails(httpServletRequest));
        return this.getAuthenticationManager().authenticate(jwtAuthenticationToken);
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

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        Assert.notNull(authenticationManager, "authenticationManager required");
        super.setAuthenticationManager(authenticationManager);
    }

    /**
     * Implementation of {@link AuthenticationDetailsSource} which builds the details object
     * from an <tt>HttpServletRequest</tt> object, creating a {@code JwtAuthenticationDetailsSource}
     * .
     */
    private static class JwtAuthenticationDetailsSource implements
            AuthenticationDetailsSource<HttpServletRequest, JwtAuthenticationTokenDetail> {

        /**
         * @param context the {@code HttpServletRequest} object.
         * @return the {@code WebAuthenticationDetails} containing information about the
         * current request
         */
        public JwtAuthenticationTokenDetail buildDetails(HttpServletRequest context) {
            return new JwtAuthenticationTokenDetail(context);
        }
    }


    /**
     * 认证失败会调用此方法，此处必须重写。因为默认会进行重定向到html页面的
     * <p>
     * <a href=https://docs.spring.io/spring-security/site/docs/5.5.2-SNAPSHOT/reference/html5/#servlet-authentication-abstractprocessingfilter>详情可阅读</a>
     *
     * @author kcl.co
     * @since 2022/02/19
     */
    private static class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            LOG.info("authenticate failure");
        }
    }

    /**
     * 认证成功会调用此方法，此处必须重写。因为默认会进行重定向到html页面的
     * <p>
     * <a href=https://docs.spring.io/spring-security/site/docs/5.5.2-SNAPSHOT/reference/html5/#servlet-authentication-abstractprocessingfilter>详情可阅读</a>
     *
     * @author kcl.co
     * @since 2022/02/19
     */
    private static class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            LOG.info("authenticate success");
        }
    }

}
