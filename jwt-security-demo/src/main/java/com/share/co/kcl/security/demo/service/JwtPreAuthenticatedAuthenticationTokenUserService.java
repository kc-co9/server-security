package com.share.co.kcl.security.demo.service;

import com.share.co.kcl.security.demo.model.entity.MockUser;
import com.share.co.kcl.security.demo.model.security.JwtUserDetails;
import com.share.co.kcl.security.demo.repository.MockUserAuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JwtPreAuthenticatedAuthenticationTokenUserService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    @Autowired
    private MockUserAuthenticationRepository authenticationRepository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
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
