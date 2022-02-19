package com.loading.server.security.api.security.authentication.entity;

import lombok.Data;

import java.util.List;

/**
 * 专属JWT Authentication Detail
 * <p>
 *
 * @author kcl.co
 * @since 2022/02/19
 */
@Data
public class JwtAuthenticationDetail {

    /**
     * phone or email
     */
    private String account;

    // more...

}
