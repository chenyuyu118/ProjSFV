package pers.cherish.commons.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisCounter {
    StringRedisTemplate stringRedisTemplate;

    public RedisCounter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public Long increaseAndGet(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public void decr(String key) {
        stringRedisTemplate.opsForValue().decrement(key);
    }
}
