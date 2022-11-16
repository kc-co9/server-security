package com.share.co.kcl.security.demo.common.model.entity;

import com.share.co.kcl.security.demo.common.model.security.JwtUserGrantedAuthority;
import lombok.Data;

import java.util.List;

@Data
public class MockUser {

    private Integer userId;

    private String username;

    private String password;

    private List<JwtUserGrantedAuthority> authorities;
}
