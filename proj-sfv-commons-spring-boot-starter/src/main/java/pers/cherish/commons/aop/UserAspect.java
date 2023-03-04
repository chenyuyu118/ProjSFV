package pers.cherish.commons.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Map;

@Component
@Aspect
public class UserAspect {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public UserAspect() {
        System.out.println("UserAspect");
    }

    @Pointcut("@annotation(pers.cherish.annotation.PermissionConfirm)")
    public void access() {
    }

    @Around("access()")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println("before");
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        final HttpServletRequest request = servletRequestAttributes.getRequest();

        final Long requestId = (Long) joinPoint.getArgs()[0];
        final String token = request.getHeader("token");
        final String s = stringRedisTemplate.opsForValue().get("token:" + token);
        if (requestId.toString().equals(s)) {
            System.out.println("验证成功");
            return joinPoint.proceed();
        } else {
            System.out.println("验证失败");
            return ResponseEntity.status(406).body(Map.of("message", "token失效"));
        }
    }
}
