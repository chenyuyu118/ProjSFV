package pers.cherish.commons.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class AOPAutoConfiguration {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @ConditionalOnProperty(prefix = "aspect", name = "type", havingValue = "user")
    @Bean
    public UserAspect userAspect() {
        UserAspect userAspect = new UserAspect();
        userAspect.setStringRedisTemplate(stringRedisTemplate);
        return userAspect;
    }

    @ConditionalOnProperty(prefix = "aspect", name = "type", havingValue = "video")
    @Bean
    public VideoAspect videoAspect() {
        return new VideoAspect();
    }
}
