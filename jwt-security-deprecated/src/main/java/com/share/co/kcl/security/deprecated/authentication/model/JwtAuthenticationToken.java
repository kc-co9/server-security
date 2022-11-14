package com.share.co.kcl.security.deprecated.authentication.model;

import com.share.co.kcl.security.common.model.JwtObject;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * principal
     */
    private final Object principal;
    /**
     * credentials
     */
    private final Object credentials;

    public JwtAuthenticationToken(JwtObject jwtObject, Object credentials) {
        super(Collections.emptyList());
        this.principal = Optional.ofNullable(jwtObject).map(JwtObject::getUserId).orElse(null);
        this.credentials = credentials;
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public JwtAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = principal;
        this.credentials = credentials;
    }


    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
