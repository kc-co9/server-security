package com.share.co.kcl.security.authentication;

import com.alibaba.fastjson.JSON;
import com.share.co.kcl.security.common.model.JwtObject;
import com.share.co.kcl.security.common.constants.RequestParamsConstants;
import com.share.co.kcl.security.common.utils.SecurityUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class JwtPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    public JwtPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(RequestParamsConstants.TOKEN))
                .map(SecurityUtils::parseToken)
                .map(JwtObject::getUserId)
                .orElse(null);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(RequestParamsConstants.TOKEN);
    }
}
