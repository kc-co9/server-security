package com.share.co.kcl.security.common.utils;

import com.alibaba.fastjson.JSON;
import com.share.co.kcl.security.common.model.JwtObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Random;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class SecurityUtils {

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private static final String SECRET = "3d990d2276917dfac04467df11fff26d";
    private static final Key SECRET_KEY = new SecretKeySpec(SECRET.getBytes(), SIGNATURE_ALGORITHM.getJcaName());

    private static final Random RAN = new Random();

    private static final String KEY_BODY = "body";
    private static final String KEY_RAN = "ran";

    private SecurityUtils() {
    }

    /**
     * 生成token
     *
     * @return 返回值
     */
    public static String echoToken(String userId) {
        JwtObject jwtObject = new JwtObject(userId);
        return Jwts.builder()
                .setSubject(null)
                .claim(KEY_BODY, JSON.toJSONString(jwtObject))
                .claim(KEY_RAN, RAN.nextInt())
                .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
                .compact();
    }

    /**
     * 解析token
     *
     * @param token 传入token
     * @return 返回解析数据
     */
    public static String parseToken(String token) {
        if (StringUtils.isBlank(token)) {
            return "";
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            return (String) claims.get(KEY_BODY);
        } catch (Exception ignoreEx) {
        }
        return "";
    }

    /**
     * 生成salt盐
     *
     * @return 返回值
     */
    public static String echoSalt() {
        return null;
    }
}
