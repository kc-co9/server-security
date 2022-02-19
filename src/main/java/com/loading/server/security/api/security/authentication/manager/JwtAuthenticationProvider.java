package com.loading.server.security.api.security.authentication.manager;

import com.loading.server.security.api.security.authentication.entity.JwtAuthentication;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * authentication really
 * <p>
 * <a href=https://docs.spring.io/spring-security/site/docs/5.5.2-SNAPSHOT/reference/html5/#servlet-authentication-authenticationprovider>详情可阅读</a>
 *
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!this.isOnline(authentication)) {
            authentication.setAuthenticated(false);
        }

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class == authentication;
    }


    /**
     * in order to limit number of online
     *
     * @param authentication 授权信息
     * @return 返回是否在线（可能被踢出）
     */
    private boolean isOnline(Authentication authentication) {
        return true;
    }

}
