package com.share.co.kcl.security.deprecated;

import com.alibaba.fastjson.JSON;
import com.share.co.kcl.security.common.model.JwtObject;
import com.share.co.kcl.security.common.utils.SecurityUtils;
import com.share.co.kcl.security.deprecated.authentication.manager.JwtAuthenticationProvider;
import com.share.co.kcl.security.deprecated.constants.MockRequestParamsConstants;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MockAuthenticationManagerFactory {

    public static AuthenticationManager createTransparentlyAuthenticateAuthenticationManager() {
        AuthenticationManager am = mock(AuthenticationManager.class);
        given(am.authenticate(any(Authentication.class)))
                .willAnswer((Answer<Authentication>) (invocation) -> (Authentication) invocation.getArguments()[0]);
        return am;
    }

    public static AuthenticationManager createNoProviderAuthenticationManager() {
        AuthenticationProvider mockProvider = mock(AuthenticationProvider.class);
        given(mockProvider.authenticate(any(Authentication.class)))
                .willAnswer((Answer<Authentication>) (invocation) -> (Authentication) invocation.getArguments()[0]);
        given(mockProvider.supports(any()))
                .willReturn(false);
        @SuppressWarnings("UnnecessaryLocalVariable")
        AuthenticationManager am = new ProviderManager(Collections.singletonList(mockProvider));
        return am;
    }

    public static AuthenticationManager createJwtProviderAuthenticationManager() {
        AuthenticationProvider mockProvider = new JwtAuthenticationProvider(token -> {
            String principal = (String) token.getPrincipal();
            String credentials = (String) token.getCredentials();
            if (Objects.isNull(principal) || "".equals(principal)) {
                throw new BadCredentialsException("bad principal");
            }
            if (Objects.isNull(credentials) || "".equals(credentials)) {
                throw new BadCredentialsException("bad credentials");
            }
            String jwtToken = SecurityUtils.parseToken(credentials);
            if (StringUtils.isBlank(jwtToken)) {
                throw new BadCredentialsException("bad credentials");
            }
            JwtObject jwtObject = JSON.parseObject(jwtToken, JwtObject.class);
            if (Objects.isNull(jwtObject)) {
                throw new BadCredentialsException("bad credentials");
            }
            if (!Objects.equals(MockRequestParamsConstants.MOCK_USER_ID, jwtObject.getUserId())) {
                throw new BadCredentialsException("bad credentials");
            }
            @SuppressWarnings("UnnecessaryLocalVariable")
            UserDetails mockDetails = MockRequestParamsConstants.MOCK_USER_DETAILS;
            return mockDetails;
        });
        @SuppressWarnings("UnnecessaryLocalVariable")
        AuthenticationManager am = new ProviderManager(Collections.singletonList(mockProvider));
        return am;
    }


}
