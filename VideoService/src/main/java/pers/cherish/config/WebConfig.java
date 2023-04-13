package pers.cherish.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pers.cherish.commons.intercepter

        .BasicInterceptor;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("#{'${variable.exclude-token-path}'.split(',')}")
    private List<String > excludePath;

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println(excludePath);
        registry.addInterceptor(new BasicInterceptor(stringRedisTemplate))
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }

}