package com.share.co.kcl.security.deprecated.demo.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(0, "成功"),

    AUTH_FAIL(401, "认证失败"),
    AUTH_DENY(402, "权限不足"),

    ERROR(500, "系统繁忙"),

    TOAST(10000, "操作失败");

    private final Integer code;
    private final String msg;
}
