package com.share.co.kcl.security.common.utils;

import com.share.co.kcl.security.common.model.JwtObject;
import org.junit.Assert;
import org.junit.Test;

public class SecurityUtilsTests {

    @Test
    public void testEchoToken() {
        String userId = "1";
        String token = SecurityUtils.echoToken(new JwtObject(userId));
        Assert.assertNotNull("token expect to be not null, but actually is", token);
    }

    @Test
    public void testParseToken() {
        String userId = "1";
        String token = SecurityUtils.echoToken(new JwtObject(userId));
        Assert.assertNotNull("token expect to be not null, but actually is", token);

        JwtObject jwtObject = SecurityUtils.parseToken(token);
        Assert.assertNotNull("jwtObject expect to be not null, but actually is", token);
        Assert.assertNotNull("JwtObject#userId expect to be not null, but actually is", jwtObject.getUserId());
    }
}
