package pers.cherish.commons.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

public class UserAspect {

    private StringRedisTemplate stringRedisTemplate;

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        System.out.println(stringRedisTemplate == null);
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public UserAspect() {
        System.out.println("UserAspect init");
    }

    @Pointcut("@annotation(pers.cherish.annotation.PermissionConfirm)")
    public void access() {
    }

    @Around("access()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
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
