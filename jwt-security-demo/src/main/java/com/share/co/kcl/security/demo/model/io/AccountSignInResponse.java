package com.share.co.kcl.security.demo.model.io;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSignInResponse {

    @ApiModelProperty(value = "token")
    private String token;

}
