package pers.cherish.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.cherish.userservice.model.UserVo;

@SpringBootTest
class UserServiceApplicationTests {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Test
    void contextLoads() {
        final UserVo userVo = new UserVo();
        rabbitTemplate.convertAndSend("user.exchange", "user.init", userVo);
    }

}
