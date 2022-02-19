package com.loading.server.security.api.security.authorization.metadatasource.impl;

import com.loading.server.security.api.security.annotation.Authorize;
import com.loading.server.security.api.security.authorization.configattribute.AuthorizeConfigAttribute;
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

    private static class AuthorizeMetadataExtractor implements AnnotationMetadataExtractor<Authorize> {

        public Collection<ConfigAttribute> extractAttributes(Authorize authorize) {
            return Collections.singletonList(new AuthorizeConfigAttribute());
        }
    }

}
