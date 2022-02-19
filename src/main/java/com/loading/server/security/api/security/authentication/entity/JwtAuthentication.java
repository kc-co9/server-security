package com.loading.server.security.api.security.authentication.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 专属JWT token
 * <p>
 * 关于{@code principal}、{@code authorities}和{@code isAuthenticated}等必须存在的值需要从构造方法中传入进去
 * 对于其他参数需要通过setXxx方法进行设置
 *
 * @author kcl.co
 * @since 2022/02/19
 */
@Getter
@Setter
public class JwtAuthentication implements Authentication {

    /**
     * principal
     */
    private String principal;
    /**
     * name
     */
    private String name;
    /**
     * authorities
     */
    private Collection<? extends GrantedAuthority> authorities;
    /**
     * credentials
     */
    private String credentials;
    /**
     * isAuthenticated
     */
    private boolean isAuthenticated;
    /**
     * isAdmin
     */
    private boolean isAdmin;
    /**
     * detail info
     */
    private JwtAuthenticationDetail details;

    public JwtAuthentication(String body) {
        // 解析body信息
    }
}
