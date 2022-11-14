package com.share.co.kcl.security.demo.model.io;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AccountSignUpRequest {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名")
    private String username;

    @NotBlank(message = "用户密码不能为空")
    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "用户权限")
    private List<String> permissions;
}
