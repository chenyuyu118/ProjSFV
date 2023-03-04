package pers.cherish.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import pers.cherish.userservice.domain.UserDTORegister;
import pers.cherish.userservice.model.User;
import pers.cherish.userservice.model.UserDTO;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
public class UserServiceDomainTest {

    @Test
    void testUserDTO() {
        final UserDTORegister register = new UserDTORegister(1L, "cherish", "我爱JAVA", "ffasdfsda", "12345678901", true, "24588@qq.com",
                "123456789", "vx123456789", "你的名字是什么", 0, null, "北京市海淀区", "北京市海淀区", "北京大学", Date.valueOf("1999-01-01"),
                 UUID.randomUUID().toString());
        User user = new User();
        BeanUtils.copyProperties(register, user, "profile");
        System.out.println(user);
    }

    @Test
    void testUserDTO1() {
        final User user = new User(1L, "cherish", "我爱JAVA", "ffasdfsda", true, "1@qq.com", "123456789", "vx123456789", 0xffffff, UUID.randomUUID().toString(), "北京市海淀区", "北京市海淀区", Timestamp.valueOf(LocalDateTime.now())
                , "你的名字是什么", Date.from(Instant.now()), "北京大学",
                false, false, "12313123");
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        System.out.println(userDTO);
    }
    @Test
    void testArray() {
        String s = "hello";
        System.out.println(s.substring(0, 3));
        System.out.println(s.substring(3));
    }

    @Test
    void testTime() {
        System.out.println(LocalDate.now().toEpochDay());
        System.out.println(LocalDate.of(1000, 1, 1).toEpochDay() & 0x7ffff);
        System.out.println(LocalDate.of(2000, 1,1).toEpochDay() & 0x7ffff);
        System.out.println(LocalDate.of(3000, 1, 1).toEpochDay() & 0x7ffff);
    }
}
