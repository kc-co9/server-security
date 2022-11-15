package com.share.co.kcl.security.deprecated.demo.controller;

import com.share.co.kcl.security.common.model.JwtObject;
import com.share.co.kcl.security.common.utils.SecurityUtils;
import com.share.co.kcl.security.deprecated.demo.exception.AccountException;
import com.share.co.kcl.security.deprecated.demo.model.entity.MockUser;
import com.share.co.kcl.security.deprecated.demo.model.io.AccountSignInRequest;
import com.share.co.kcl.security.deprecated.demo.model.io.AccountSignInResponse;
import com.share.co.kcl.security.deprecated.demo.model.io.AccountSignUpRequest;
import com.share.co.kcl.security.deprecated.demo.model.security.JwtUserGrantedAuthority;
import com.share.co.kcl.security.deprecated.demo.repository.MockUserAuthenticationRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(tags = "账号路由")
@RequestMapping(value = "/account")
public class AccountController {

    @Autowired
    private MockUserAuthenticationRepository authenticationRepository;

    @ApiOperation(value = "注册")
    @PostMapping(value = "/v1/signUp")
    public void signUp(@RequestBody @Validated AccountSignUpRequest request) {
        MockUser user = authenticationRepository.get(request.getUsername());
        if (Objects.nonNull(user)) {
            throw new AccountException("账号已存在");
        }
        List<JwtUserGrantedAuthority> authorities =
                Optional.ofNullable(request.getPermissions()).orElse(Collections.emptyList())
                        .stream()
                        .map(JwtUserGrantedAuthority::new)
                        .collect(Collectors.toList());
        authenticationRepository.add(request.getUsername(), request.getPassword(), authorities);
    }

    @ApiOperation(value = "登陆")
    @PostMapping(value = "/v1/signIn")
    public AccountSignInResponse signIn(@RequestBody @Validated AccountSignInRequest request) {
        MockUser user = authenticationRepository.get(request.getUsername());
        if (Objects.isNull(user)) {
            throw new AccountException("账号不存在");
        }
        if (!Objects.equals(user.getPassword(), request.getPassword())) {
            throw new AccountException("账号或密码不正确");
        }
        return new AccountSignInResponse(
                SecurityUtils.echoToken(new JwtObject(String.valueOf(user.getUserId()))));
    }

}
