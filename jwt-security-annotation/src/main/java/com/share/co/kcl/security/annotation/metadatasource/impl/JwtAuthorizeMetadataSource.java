package com.share.co.kcl.security.annotation.metadatasource.impl;

import com.share.co.kcl.security.annotation.Auth;
import com.share.co.kcl.security.annotation.configattribute.AuthorizeConfigAttribute;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;

import java.util.Collection;
import java.util.Collections;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtAuthorizeMetadataSource extends SecuredAnnotationSecurityMetadataSource {
    public JwtAuthorizeMetadataSource() {
        super(new AuthorizeMetadataExtractor());
    }

    public JwtAuthorizeMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        super(annotationMetadataExtractor);
    }

    private static class AuthorizeMetadataExtractor implements AnnotationMetadataExtractor<Auth> {

        public Collection<ConfigAttribute> extractAttributes(Auth auth) {
            return Collections.singletonList(new AuthorizeConfigAttribute());
        }
    }

}
