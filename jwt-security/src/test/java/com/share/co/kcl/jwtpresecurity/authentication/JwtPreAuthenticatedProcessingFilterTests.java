package com.share.co.kcl.jwtpresecurity.authentication;

import com.alibaba.fastjson.JSON;
import com.share.co.kcl.jwtpresecurity.MockAuthenticationFactory;
import com.share.co.kcl.jwtpresecurity.MockAuthenticationManagerFactory;
import com.share.co.kcl.jwtpresecurity.MockJwtPreAuthenticatedProcessingFilterFactory;
import com.share.co.kcl.jwtpresecurity.constants.MockRequestParamsConstants;
import com.share.co.kcl.security.authentication.JwtPreAuthenticatedProcessingFilter;
import com.share.co.kcl.security.common.constants.RequestParamsConstants;
import com.share.co.kcl.security.common.model.JwtObject;
import com.share.co.kcl.security.common.utils.SecurityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;


public class JwtPreAuthenticatedProcessingFilterTests {

    @Before
    public void doBefore() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testFilterNoMatch() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        JwtPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter =
                new JwtPreAuthenticatedProcessingFilter(MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager());
        jwtPreAuthenticatedProcessingFilter.setRequiresAuthenticationRequestMatcher(any -> false);
        jwtPreAuthenticatedProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNull("authentication expect to be null, but actually isn't", authentication);
    }

    @Test
    public void testFilterNoMatchForAuthenticationExist() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        SecurityContextHolder.getContext().setAuthentication(MockAuthenticationFactory.createAnonymousAuthentication());

        JwtPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter =
                new JwtPreAuthenticatedProcessingFilter(MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager());
        jwtPreAuthenticatedProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("authentication expect to be not null, but actually is", authentication);
        Assert.assertEquals("authentication expect to be AnonymousAuthenticationToken, but actually isn't", AnonymousAuthenticationToken.class, authentication.getClass());
    }

    @Test
    public void testFilterNoMatchForPrincipalNoChanged() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        SecurityContextHolder.getContext().setAuthentication(MockAuthenticationFactory.createAnonymousAuthentication());

        AuthenticationManager am = MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager();
        JwtPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter =
                MockJwtPreAuthenticatedProcessingFilterFactory.createFilterWithoutPrincipalChanged(am);
        jwtPreAuthenticatedProcessingFilter.setCheckForPrincipalChanges(true);
        jwtPreAuthenticatedProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("authentication expect to be not null, but actually is", authentication);
        Assert.assertEquals("authentication expect to be AnonymousAuthenticationToken, but actually isn't", AnonymousAuthenticationToken.class, authentication.getClass());
    }


    @Test
    public void testFilterMatch() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        AuthenticationManager am = MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager();
        JwtPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter =
                MockJwtPreAuthenticatedProcessingFilterFactory.createExistPrincipalAndCredentialsFilter(am);
        jwtPreAuthenticatedProcessingFilter.setRequiresAuthenticationRequestMatcher(any -> true);
        jwtPreAuthenticatedProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("authentication expect to be not null, but actually is", authentication);
        Assert.assertEquals("authentication expect to be AnonymousAuthenticationToken, but actually isn't", PreAuthenticatedAuthenticationToken.class, authentication.getClass());
    }

    @Test
    public void testFilterMatchForAuthenticationNotExist() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        SecurityContextHolder.clearContext();

        AuthenticationManager am = MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager();
        JwtPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter =
                MockJwtPreAuthenticatedProcessingFilterFactory.createExistPrincipalAndCredentialsFilter(am);
        jwtPreAuthenticatedProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("authentication expect to be not null, but actually is", authentication);
        Assert.assertEquals("authentication expect to be AnonymousAuthenticationToken, but actually isn't", PreAuthenticatedAuthenticationToken.class, authentication.getClass());
    }

    @Test
    public void testFilterMatchForPrincipalChanged() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        SecurityContextHolder.getContext().setAuthentication(MockAuthenticationFactory.createAnonymousAuthentication());

        AuthenticationManager am = MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager();
        JwtPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter =
                MockJwtPreAuthenticatedProcessingFilterFactory.createExistPrincipalAndCredentialsFilterWithPrincipalChanged(am);
        jwtPreAuthenticatedProcessingFilter.setCheckForPrincipalChanges(true);
        jwtPreAuthenticatedProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("authentication expect to be not null, but actually is", authentication);
        Assert.assertEquals("authentication expect to be AnonymousAuthenticationToken, but actually isn't", PreAuthenticatedAuthenticationToken.class, authentication.getClass());
    }

    @Test
    public void testAuthenticateNoPrincipal() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        AuthenticationManager am = MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager();
        JwtPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter =
                MockJwtPreAuthenticatedProcessingFilterFactory.createExistCredentialsFilter(am);
        jwtPreAuthenticatedProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNull("authentication expect to be null, but actually isn't", authentication);
    }

    @Test
    public void testAuthenticateNoCredentials() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        AuthenticationManager am = MockAuthenticationManagerFactory.createTransparentlyAuthenticateAuthenticationManager();
        JwtPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter =
                MockJwtPreAuthenticatedProcessingFilterFactory.createExistPrincipalFilter(am);
        jwtPreAuthenticatedProcessingFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("authentication expect to be not null, but actually is", authentication);
        Assert.assertEquals("authentication expect to be AnonymousAuthenticationToken, but actually isn't", PreAuthenticatedAuthenticationToken.class, authentication.getClass());
    }

    @Test
    public void testAuthenticateNoSuitableAuthenticationProvider() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        try {
            AuthenticationManager am = MockAuthenticationManagerFactory.createNoProviderAuthenticationManager();
            JwtPreAuthenticatedProcessingFilter jwtAuthenticationProcessingFilter =
                    MockJwtPreAuthenticatedProcessingFilterFactory.createExistPrincipalAndCredentialsFilter(am);
            jwtAuthenticationProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
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
    public void testAuthenticateSuccess() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader(RequestParamsConstants.TOKEN, MockRequestParamsConstants.MOCK_TOKEN);

        try (MockedStatic<SecurityUtils> mockStatic = mockStatic(SecurityUtils.class)) {
            mockStatic.when(() -> SecurityUtils.parseToken(MockRequestParamsConstants.MOCK_TOKEN))
                    .thenAnswer((Answer<String>) invocation -> JSON.toJSONString(new JwtObject(MockRequestParamsConstants.MOCK_USER_ID)));

            AuthenticationManager am = MockAuthenticationManagerFactory.createPreAuthenticatedAuthenticationProviderAuthenticationManager();
            JwtPreAuthenticatedProcessingFilter jwtAuthenticationProcessingFilter = MockJwtPreAuthenticatedProcessingFilterFactory.createFilter(am);
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
    public void testAuthenticateFailureWithoutToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        try (MockedStatic<SecurityUtils> mockStatic = mockStatic(SecurityUtils.class)) {
            mockStatic.when(() -> SecurityUtils.parseToken(any())).thenAnswer(invocation -> null);

            AuthenticationManager am = MockAuthenticationManagerFactory.createPreAuthenticatedAuthenticationProviderAuthenticationManager();
            JwtPreAuthenticatedProcessingFilter jwtAuthenticationProcessingFilter = MockJwtPreAuthenticatedProcessingFilterFactory.createFilter(am);
            jwtAuthenticationProcessingFilter.doFilter(request, response, filterChain);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertNull("authentication expect to be null, but actually isn't", authentication);
        }
    }

    @Test
    public void testAuthenticateFailureWithToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/any");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader(RequestParamsConstants.TOKEN, MockRequestParamsConstants.MOCK_ERROR_TOKEN);

        try (MockedStatic<SecurityUtils> mockStatic = mockStatic(SecurityUtils.class)) {
            mockStatic.when(() -> SecurityUtils.parseToken(MockRequestParamsConstants.MOCK_ERROR_TOKEN))
                    .thenAnswer((Answer<String>) invocation -> JSON.toJSONString(new JwtObject(MockRequestParamsConstants.MOCK_ERROR_USER_ID)));

            AuthenticationManager am = MockAuthenticationManagerFactory.createPreAuthenticatedAuthenticationProviderAuthenticationManager();
            JwtPreAuthenticatedProcessingFilter jwtAuthenticationProcessingFilter = MockJwtPreAuthenticatedProcessingFilterFactory.createFilter(am);
            jwtAuthenticationProcessingFilter.doFilter(request, response, filterChain);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertNull("authentication expect to be null, but actually isn't", authentication);
        }
    }
}
