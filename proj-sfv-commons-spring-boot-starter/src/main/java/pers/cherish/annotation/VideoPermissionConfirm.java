package pers.cherish.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// 运行时
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
public @interface VideoPermissionConfirm {
    String value() default "视频权限确认";
}
