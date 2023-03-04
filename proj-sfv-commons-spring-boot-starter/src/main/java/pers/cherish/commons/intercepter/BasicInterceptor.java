package pers.cherish.commons.intercepter;

import com.alibaba.fastjson2.JSON;
import com.rabbitmq.tools.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;


public class BasicInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public BasicInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String authorization = request.getHeader("token");
        System.out.println("interceptor!");
        if (authorization == null) {
            final byte[] bytes = JSON.toJSONBytes(Map.of("message", "请先登陆"));
            response.setStatus(406);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getOutputStream().write(bytes);
            return false;
        } else {
            final String id = stringRedisTemplate.opsForValue().get("token:" + authorization);
            if (id == null) {
                final byte[] bytes = JSON.toJSONBytes(Map.of("message", "token过期"));
                response.setStatus(406);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getOutputStream().write(bytes);
                return false;
            }
            return true;
        }
//        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
