package com.share.co.kcl.security.deprecated.demo.model.security;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtUserGrantedAuthority implements GrantedAuthority, Serializable {

    private final String permission;

    public JwtUserGrantedAuthority(String permission) {
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return this.permission;
    }
}
