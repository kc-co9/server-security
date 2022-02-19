package com.loading.server.security.api.security.authorization.decision;

import com.loading.server.security.api.security.authentication.entity.JwtAuthentication;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class JwtUnanimousBased extends UnanimousBased {

    public JwtUnanimousBased(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException {
        if (Objects.nonNull(authentication)
                && authentication instanceof JwtAuthentication
                && ((JwtAuthentication) authentication).isAdmin()) {
            // 超级管理员不受权限限制
            return;
        }
        super.decide(authentication, object, configAttributes);
    }
}
