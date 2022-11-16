package com.share.co.kcl.security.demo.deprecated.controller;

import com.share.co.kcl.security.annotation.Auth;
import com.share.co.kcl.security.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "访问路由")
@RequestMapping(value = "/example")
public class ExampleController {

    @ApiOperation(value = "匿名访问")
    @PostMapping(value = "/v1/anonymousAccess")
    public void anonymousAccess() {
    }

    @Auth
    @ApiOperation(value = "认证访问")
    @PostMapping(value = "/v1/authAccess")
    public void authAccess() {
    }

    @Auth
    @ApiOperation(value = "权限访问")
    @Permission(value = {"permission:access"})
    @PostMapping(value = "/v1/permissionAccess")
    public void permissionAccess() {
    }

}
