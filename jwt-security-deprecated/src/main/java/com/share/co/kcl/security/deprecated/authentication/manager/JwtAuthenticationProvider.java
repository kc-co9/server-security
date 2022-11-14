package com.share.co.kcl.security.deprecated.authentication.manager;

import com.share.co.kcl.security.deprecated.authentication.model.JwtAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * authentication really
 * <p>
 * <a href=https://docs.spring.io/spring-security/site/docs/5.5.2-SNAPSHOT/reference/html5/#servlet-authentication-authenticationprovider>详情可阅读</a>
 *
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final Authentication ANONYMOUS_AUTHENTICATION_TOKEN =
            new AnonymousAuthenticationToken("anonymous", "anonymous", AuthorityUtils.createAuthorityList("anonymous"));

    private final AuthenticationUserDetailsService<JwtAuthenticationToken> authenticationUserDetailsService;

    public JwtAuthenticationProvider(AuthenticationUserDetailsService<JwtAuthenticationToken> authenticationUserDetailsService) {
        this.authenticationUserDetailsService = authenticationUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        try {
            UserDetails userDetails = authenticationUserDetailsService.loadUserDetails(jwtAuthenticationToken);
            JwtAuthenticationToken result = new JwtAuthenticationToken(
                    userDetails, jwtAuthenticationToken.getCredentials(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(result);
            return result;
        } catch (AuthenticationException ignoreEx) {
            return ANONYMOUS_AUTHENTICATION_TOKEN;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class == authentication;
    }
}
