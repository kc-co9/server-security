package com.share.co.kcl.jwtpresecurity;

import com.share.co.kcl.security.authentication.JwtPreAuthenticatedProcessingFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public class MockJwtPreAuthenticatedProcessingFilterFactory {

    public static JwtPreAuthenticatedProcessingFilter createFilter(AuthenticationManager am) {
        return new JwtPreAuthenticatedProcessingFilter(am);
    }

    public static JwtPreAuthenticatedProcessingFilter createFilterWithoutPrincipalChanged(AuthenticationManager am) {
        return new JwtPreAuthenticatedProcessingFilter(am) {
            @Override
            protected boolean principalChanged(HttpServletRequest request, Authentication currentAuthentication) {
                return false;
            }
        };
    }

    public static JwtPreAuthenticatedProcessingFilter createExistPrincipalFilter(AuthenticationManager am) {
        return new JwtPreAuthenticatedProcessingFilter(am) {
            @Override
            protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
                return new Object();
            }

            @Override
            protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
                return null;
            }
        };
    }

    public static JwtPreAuthenticatedProcessingFilter createExistCredentialsFilter(AuthenticationManager am) {
        return new JwtPreAuthenticatedProcessingFilter(am) {
            @Override
            protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
                return null;
            }

            @Override
            protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
                return new Object();
            }
        };
    }

    public static JwtPreAuthenticatedProcessingFilter createExistPrincipalAndCredentialsFilter(AuthenticationManager am) {
        return new JwtPreAuthenticatedProcessingFilter(am) {
            @Override
            protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
                return new Object();
            }

            @Override
            protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
                return new Object();
            }
        };
    }


    public static JwtPreAuthenticatedProcessingFilter createExistPrincipalAndCredentialsFilterWithPrincipalChanged(AuthenticationManager am) {
        return new JwtPreAuthenticatedProcessingFilter(am) {
            @Override
            protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
                return new Object();
            }

            @Override
            protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
                return new Object();
            }

            @Override
            protected boolean principalChanged(HttpServletRequest request, Authentication currentAuthentication) {
                return true;
            }
        };
    }
}
