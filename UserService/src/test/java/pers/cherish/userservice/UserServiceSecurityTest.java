package pers.cherish.userservice;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

public class UserServiceSecurityTest {

    @Test
    void testEncode() {
        final String s = DigestUtils.md5Hex("cherish" + "123456");
        System.out.println(s);
    }
}
