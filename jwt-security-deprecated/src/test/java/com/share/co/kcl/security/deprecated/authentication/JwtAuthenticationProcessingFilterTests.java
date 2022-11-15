package com.share.co.kcl.security.deprecated.authentication;

import com.alibaba.fastjson.JSON;
import com.share.co.kcl.security.common.constants.RequestParamsConstants;
import com.share.co.kcl.security.common.model.JwtObject;
import com.share.co.kcl.security.common.utils.SecurityUtils;
import com.share.co.kcl.security.deprecated.MockAuthenticationManagerFactory;
import com.share.co.kcl.security.deprecated.constants.MockRequestParamsConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JwtAuthenticationProcessingFilterTests {

    @Before
    public void doBefore() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testNullableAuthenticationManager() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        try {
            // AuthenticationManager is null
            AuthenticationManager am = null;
            @SuppressWarnings("ConstantConditions")
            JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(am);
            jwtAuthenticationProcessingFilter.doFilter(request, response, filterChain);
        } catch (Exception exception) {
            Assert.assertEquals("exception expect to be IllegalArgumentException, but actually is " + exception.getClass().getName(), IllegalArgumentException.class, exception.getClass());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertNull("authentication expect to be null, but actually isn't", authentication);

            return;
        }
        Assert.fail("expect throw exception");
    }

    @Test
    public void testNoSuitableAuthenticationManager() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        AuthenticationManager am = MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager();
        JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(am);
        jwtAuthenticationProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNull("authentication expect to be null, but actually isn't", authentication);
    }

    @Test
    public void testNoSuitableAuthenticationProvider() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        try {
            AuthenticationManager am = MockAuthenticationManagerFactory.createNoProviderAuthenticationManager();
            JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(am);
            jwtAuthenticationProcessingFilter.setAuthenticationFailureHandler((req, resp, exception) -> {
                throw exception;
            });
            jwtAuthenticationProcessingFilter.doFilter(request, response, filterChain);
        } catch (Exception ex) {
            Assert.assertEquals("exception expect to be ProviderNotFoundException, but actually is " + ex.getClass().getName(), ProviderNotFoundException.class, ex.getClass());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertNull("authentication expect to be null, but actually isn't", authentication);

            return;
        }
        Assert.fail("expect throw exception");
    }

    @Test
    public void testAttemptAuthenticationSuccess() throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader(RequestParamsConstants.TOKEN, MockRequestParamsConstants.MOCK_TOKEN);

        try (MockedStatic<SecurityUtils> mockStatic = mockStatic(SecurityUtils.class)) {
            mockStatic.when(() -> SecurityUtils.parseToken(MockRequestParamsConstants.MOCK_TOKEN))
                    .thenAnswer((Answer<JwtObject>) invocation -> new JwtObject(MockRequestParamsConstants.MOCK_USER_ID));

            AuthenticationManager am = MockAuthenticationManagerFactory.createJwtProviderAuthenticationManager();
            JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(am);
            jwtAuthenticationProcessingFilter.doFilter(request, response, filterChain);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertNotNull("authentication expect to be not null, but actually is", authentication);
            Assert.assertNotNull("authentication principal expect to be not null, but actually is", authentication.getPrincipal());
            Assert.assertNotNull("authentication authorities expect to be not null, but actually is", authentication.getAuthorities());
            Assert.assertNotNull("authentication credentials expect to be not null, but actually is", authentication.getCredentials());
            Assert.assertNotNull("authentication details expect to be not null, but actually is", authentication.getDetails());
        }
    }

    @Test
    public void testAttemptAuthenticationFailureWithoutToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        try (MockedStatic<SecurityUtils> mockStatic = mockStatic(SecurityUtils.class)) {
            mockStatic.when(() -> SecurityUtils.parseToken(any())).thenAnswer(invocation -> null);

            AuthenticationManager am = MockAuthenticationManagerFactory.createJwtProviderAuthenticationManager();
            JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(am);
            jwtAuthenticationProcessingFilter.doFilter(request, response, filterChain);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertNull("authentication expect to be null, but actually isn't", authentication);
        }
    }

    @Test
    public void testAttemptAuthenticationFailureWithToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader(RequestParamsConstants.TOKEN, MockRequestParamsConstants.MOCK_ERROR_TOKEN);

        try (MockedStatic<SecurityUtils> mockStatic = mockStatic(SecurityUtils.class)) {
            mockStatic.when(() -> SecurityUtils.parseToken(MockRequestParamsConstants.MOCK_ERROR_TOKEN))
                    .thenAnswer((Answer<JwtObject>) invocation -> new JwtObject(MockRequestParamsConstants.MOCK_ERROR_USER_ID));

            AuthenticationManager am = MockAuthenticationManagerFactory.createJwtProviderAuthenticationManager();
            JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(am);
            jwtAuthenticationProcessingFilter.doFilter(request, response, filterChain);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertNull("authentication expect to be null, but actually isn't", authentication);
        }
    }

}
