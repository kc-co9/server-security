package com.share.co.kcl.security.annotation.configattribute;

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

