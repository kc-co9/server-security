package com.share.co.kcl.jwtpresecurity.constants;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

public class MockRequestParamsConstants {

    public static final String MOCK_ERROR_TOKEN = "this is a error token";

    public static final String MOCK_ERROR_USER_ID = "9999";

    public static final String MOCK_TOKEN = "this is a mock token";

    public static final String MOCK_USER_ID = "8888";

    public static final String MOCK_USER_NAME = "this is a mock username";

    public static final String MOCK_USER_PASSWORD = "this is a mock password";

    public static final List<? extends GrantedAuthority> MOCK_USER_AUTHORITY =
            Arrays.asList(new SimpleGrantedAuthority("TEST_AUTHORITY_1"), new SimpleGrantedAuthority("TEST_AUTHORITY_2"));

    public static final UserDetails MOCK_USER_DETAILS =
            new User(MOCK_USER_NAME, MOCK_USER_PASSWORD, MOCK_USER_AUTHORITY);
}
