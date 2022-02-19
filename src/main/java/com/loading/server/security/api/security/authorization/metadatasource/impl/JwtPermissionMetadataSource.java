package com.loading.server.security.api.security.authorization.metadatasource.impl;

import com.loading.server.security.api.security.annotation.Permission;
import com.loading.server.security.api.security.authorization.configattribute.PermissionConfigAttribute;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtPermissionMetadataSource extends SecuredAnnotationSecurityMetadataSource {

    public JwtPermissionMetadataSource() {
        super(new PermissionMetadataExtractor());
    }

    public JwtPermissionMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        super(annotationMetadataExtractor);
    }

    private static class PermissionMetadataExtractor implements AnnotationMetadataExtractor<Permission> {

        public Collection<ConfigAttribute> extractAttributes(Permission permission) {
            return Arrays.stream(permission.value())
                    .map(PermissionConfigAttribute::new)
                    .collect(Collectors.toList());
        }
    }
}
