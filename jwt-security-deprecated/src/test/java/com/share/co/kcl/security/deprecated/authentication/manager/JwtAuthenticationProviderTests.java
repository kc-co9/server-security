package com.share.co.kcl.security.deprecated.authentication.manager;

import com.share.co.kcl.security.common.model.JwtObject;
import com.share.co.kcl.security.deprecated.authentication.model.JwtAuthenticationToken;
import com.share.co.kcl.security.deprecated.constants.MockRequestParamsConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class JwtAuthenticationProviderTests {

    @Before
    public void doBefore() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testAuthenticateSuccess() {
        JwtObject jwtObject = new JwtObject(MockRequestParamsConstants.MOCK_USER_ID);
        JwtAuthenticationToken jwtAuthenticationToken =
                new JwtAuthenticationToken(jwtObject, MockRequestParamsConstants.MOCK_TOKEN);

        @SuppressWarnings("unchecked")
        AuthenticationUserDetailsService<JwtAuthenticationToken> authenticationUserDetailsService = mock(AuthenticationUserDetailsService.class);
        given(authenticationUserDetailsService.loadUserDetails(jwtAuthenticationToken))
                .willAnswer((Answer<UserDetails>) invocation -> MockRequestParamsConstants.MOCK_USER_DETAILS);

        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(authenticationUserDetailsService);
        jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("authentication expect to be not null, but actually is", authentication);
        Assert.assertEquals("authentication expect to be MOCK_USER_DETAILS, but actually isn't", MockRequestParamsConstants.MOCK_USER_DETAILS, authentication.getPrincipal());
    }

    @Test
    public void testAuthenticateFailure() {
        JwtObject jwtObject = new JwtObject(MockRequestParamsConstants.MOCK_ERROR_USER_ID);
        JwtAuthenticationToken jwtAuthenticationToken =
                new JwtAuthenticationToken(jwtObject, MockRequestParamsConstants.MOCK_ERROR_TOKEN);

        @SuppressWarnings("unchecked")
        AuthenticationUserDetailsService<JwtAuthenticationToken> authenticationUserDetailsService = mock(AuthenticationUserDetailsService.class);
        given(authenticationUserDetailsService.loadUserDetails(jwtAuthenticationToken))
                .willThrow(new BadCredentialsException("bad credentials"));

        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(authenticationUserDetailsService);
        jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNull("authentication expect to be null, but actually isn't", authentication);
    }
}
