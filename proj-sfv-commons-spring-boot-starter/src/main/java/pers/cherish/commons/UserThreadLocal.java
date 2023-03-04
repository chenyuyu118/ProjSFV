package pers.cherish.commons;


import org.springframework.stereotype.Component;
import pers.cherish.userservice.model.User;

@Component
public class UserThreadLocal {
    ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void addUser(User user) {
        userThreadLocal.set(user);
    }

    public User getUser() {
        return userThreadLocal.get();
    }

    public void removeUser() {
        userThreadLocal.remove();
    }
}
