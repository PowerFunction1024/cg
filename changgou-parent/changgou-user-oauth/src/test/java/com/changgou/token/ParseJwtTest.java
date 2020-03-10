package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.xu
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token ="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.iQpT6Yaa7sLYSixdGDFs2ogFAOyGxJfnAyG0HzFohmnQElpnwQHdOFlVBSIXT5e_Z4W2qZgYFMJ634DGkraX9TI6VYzxUL3gRpWU2BDVO0nl4AqL1gfyd0DtZtc7nzhLQJvtTgch1Y1vq1Pe0g0R6io_ehnaeQBQPO86V7iawawN9nc37IKr7HT4BXuSdni-xfw__eKSHHEKT79wRDDFFAuc9v5ehKP97T4ykLTDKVIAgo42yJJUbCPDCCsrAe2UWK9qQCXw4pcvvv6m11nx69biAcSbGszLjbou_EPLAG33Mb1Va2saCX3gWRGKlEA3TzsGgzsW88eRZET5ihD85g";
        String publickey ="-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkCYhWLtB1lhAkeTdOMQaW46G30+dvRShBPJY2k7XkJY44lD6rClxJGrBjrhfOcH2q8jKYCSlXyuffgTjVOQrfq5musutY0sVU7WTjUmJIj6kKdWdJqPVeZJSJRnGtUAe1MCMq7jp6YRC+tlH+pNg/e7UOAFOULrJxnazA0Apvk4IjOjNeGesIfbug/y8okhp1CNqt4dWhI1xqzgOSSCJBeP8uORBqtPlwsfEf8B1dExG32eotCNXJVyTaklGLnAfOQurl2CyE8SPcJtHZl+sEbalmRCEJKRmVyyLHRjrVetFw08Kr71x8SxjMw/S+KbVj008ZCTCD2pVV5cIiOom4QIDAQAB-----END PUBLIC KEY-----";
        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
