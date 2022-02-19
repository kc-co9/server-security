package com.loading.server.security.api.restful;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/account")
public class AccountRest {

    @PostMapping(value = "/v1/login")
    public Object passwordLogin(@RequestBody @Validated Object request) {
        return null;
    }

}
