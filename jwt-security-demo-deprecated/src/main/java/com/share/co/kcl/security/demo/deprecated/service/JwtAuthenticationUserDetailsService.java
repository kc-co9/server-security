package com.share.co.kcl.security.demo.deprecated.service;

import com.share.co.kcl.security.demo.common.model.entity.MockUser;
import com.share.co.kcl.security.demo.common.model.security.JwtUserDetails;
import com.share.co.kcl.security.demo.common.repository.MockUserAuthenticationRepository;
import com.share.co.kcl.security.deprecated.authentication.model.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JwtAuthenticationUserDetailsService implements AuthenticationUserDetailsService<JwtAuthenticationToken> {

    @Autowired
    private MockUserAuthenticationRepository authenticationRepository;

    @Override
    public UserDetails loadUserDetails(JwtAuthenticationToken token) throws AuthenticationException {
        if (Objects.isNull(token)) {
            throw new BadCredentialsException("required token");
        }
        if (Objects.isNull(token.getPrincipal())) {
            throw new BadCredentialsException("principal is error");
        }
        MockUser user = authenticationRepository.get(Integer.parseInt(String.valueOf(token.getPrincipal())));
        if (Objects.isNull(user)) {
            throw new BadCredentialsException("user is not exist");
        }
        @SuppressWarnings("UnnecessaryLocalVariable")
        JwtUserDetails jwtUserDetails = new JwtUserDetails(
                user.getUserId(), user.getUsername(), user.getPassword(), user.getAuthorities());
        return jwtUserDetails;
    }
}
