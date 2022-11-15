package com.share.co.kcl.jwtpresecurity;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

public class MockAuthenticationFactory {

    public static Authentication createAnonymousAuthentication() {
        return new AnonymousAuthenticationToken("anonymous", "anonymous", AuthorityUtils.createAuthorityList("anonymous"));
    }
}
