package com.loading.server.security.api.security.authorization.configattribute;

import org.springframework.security.access.ConfigAttribute;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class AuthorizeConfigAttribute implements ConfigAttribute {
    @Override
    public String getAttribute() {
        return "";
    }
}

