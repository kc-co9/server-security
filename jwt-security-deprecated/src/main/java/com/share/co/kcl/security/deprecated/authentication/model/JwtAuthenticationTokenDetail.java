package com.share.co.kcl.security.deprecated.authentication.model;

import lombok.Getter;
import org.springframework.security.core.SpringSecurityCoreVersion;

import javax.servlet.http.HttpServletRequest;

/**
 * 专属JWT Authentication Detail
 * <p>
 *
 * @author kcl.co
 * @since 2022/02/19
 */
@Getter
public class JwtAuthenticationTokenDetail {


    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // ~ Instance fields
    // ================================================================================================

    private final String remoteAddress;

    // ~ Constructors
    // ===================================================================================================

    /**
     * Records the remote address and will also set the session Id if a session already
     * exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public JwtAuthenticationTokenDetail(HttpServletRequest request) {
        this.remoteAddress = request.getRemoteAddr();
    }

}
