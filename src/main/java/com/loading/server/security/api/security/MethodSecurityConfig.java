package com.loading.server.security.api.security;

import com.loading.server.security.api.security.authorization.metadatasource.JwtDelegatingMetadataSource;
import com.loading.server.security.api.security.authorization.decision.JwtUnanimousBased;
import com.loading.server.security.api.security.authorization.metadatasource.impl.JwtAuthorizeMetadataSource;
import com.loading.server.security.api.security.authorization.metadatasource.impl.JwtPermissionMetadataSource;
import com.loading.server.security.api.security.authorization.voter.AuthorizeVoter;
import com.loading.server.security.api.security.authorization.voter.PermissionVoter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        AccessDecisionManager accessDecisionManager = super.accessDecisionManager();
        if (Objects.nonNull(accessDecisionManager)
                && accessDecisionManager instanceof AffirmativeBased) {
            AffirmativeBased affirmativeBased = (AffirmativeBased) accessDecisionManager;
            List<AccessDecisionVoter<?>> voters = new ArrayList<>();
            voters.add(new AuthorizeVoter());
            voters.add(new PermissionVoter());
            voters.addAll(affirmativeBased.getDecisionVoters());
            return new JwtUnanimousBased(voters);
        }

        return accessDecisionManager;
    }

    @Bean
    @Override
    public MethodSecurityMetadataSource methodSecurityMetadataSource() {
        DelegatingMethodSecurityMetadataSource delegating = (DelegatingMethodSecurityMetadataSource) super.methodSecurityMetadataSource();
        List<MethodSecurityMetadataSource> metadataSourceList = new ArrayList<>();
        metadataSourceList.add(new JwtAuthorizeMetadataSource());
        metadataSourceList.add(new JwtPermissionMetadataSource());
        metadataSourceList.addAll(delegating.getMethodSecurityMetadataSources());
        return new JwtDelegatingMetadataSource(metadataSourceList);
    }


}
