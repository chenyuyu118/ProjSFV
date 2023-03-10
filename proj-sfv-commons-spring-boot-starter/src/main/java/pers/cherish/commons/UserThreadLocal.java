package pers.cherish.commons;


import org.springframework.stereotype.Component;

@Component
public class UserThreadLocal {
    private static ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

    public static void addUser(Long user) {
        userThreadLocal.set(user);
    }

    public static Long getUser() {
        return userThreadLocal.get();
    }

    public static void removeUser() {
        userThreadLocal.remove();
    }
}
