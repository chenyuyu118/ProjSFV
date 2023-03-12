package pers.cherish.commons.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class CommentAspect {

    @Pointcut("@annotation(pers.cherish.annotation.VideoExistConfirm)")
    public void access() {
    }


}
