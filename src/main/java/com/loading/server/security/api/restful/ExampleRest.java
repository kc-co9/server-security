package com.loading.server.security.api.restful;

import com.loading.server.security.api.security.annotation.Authorize;
import com.loading.server.security.api.security.annotation.Permission;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/example")
public class ExampleRest {

    @Authorize
    @Permission(value = {"permission:access"})
    @PostMapping(value = "/v1/access")
    public Object access() {
        return null;
    }
}
