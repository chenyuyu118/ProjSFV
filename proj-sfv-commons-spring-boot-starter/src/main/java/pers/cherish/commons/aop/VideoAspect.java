package pers.cherish.commons.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.cherish.annotation.VideoPermissionConfirm;

import java.util.Map;


@Aspect
public class VideoAspect {
    public VideoAspect() {
        System.out.println("VideoAspect init");
    }

    private StringRedisTemplate stringRedisTemplate;

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Pointcut("@annotation(pers.cherish.annotation.VideoPermissionConfirm)")
    public void access() {}

    @Around("access() && @annotation(annotation)")
    public Object doAround(ProceedingJoinPoint joinPoint, VideoPermissionConfirm annotation) throws Throwable {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        final HttpServletRequest request = servletRequestAttributes.getRequest();

//        final MethodSignature methodSignature = (MethodSignature) signature;
//        final Object target = joinPoint.getTarget();
//        final Method method = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
//        final VideoPermissionConfirm annotation = method.getAnnotation(VideoPermissionConfirm.class);

        if (annotation.value().equals("user")) {
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
        } else if (annotation.value().equals("video")) {
            final String videoId = (String) joinPoint.getArgs()[0];
            final String s = stringRedisTemplate.opsForValue().get(videoId);
            return joinPoint.proceed();
        }
        return null;
    }
}
