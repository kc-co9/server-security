package com.loading.server.security.api.security.authentication.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtGrantedAuthority implements GrantedAuthority {

    private final String permission;

    public JwtGrantedAuthority(String permission) {
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return permission;
    }
}
