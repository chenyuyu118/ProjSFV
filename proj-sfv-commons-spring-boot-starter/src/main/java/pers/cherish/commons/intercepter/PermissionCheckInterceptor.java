package pers.cherish.commons.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


@Deprecated
public class PermissionCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(request.getPathInfo());
        System.out.println(request.getRequestURI());
        System.out.println(request.getPathTranslated());
        System.out.println(request.getContextPath());
        System.out.println(request.getServletPath());
        return true;
    }

}
