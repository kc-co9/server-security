package com.loading.server.security.api.security.authorization.configattribute;

import org.springframework.security.access.SecurityConfig;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class PermissionConfigAttribute extends SecurityConfig {

    public PermissionConfigAttribute(String config) {
        super(config);
    }
}
