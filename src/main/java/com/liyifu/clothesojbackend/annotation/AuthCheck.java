package com.liyifu.clothesojbackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 * @Retention 和 @Targe  注解可以自定义注解
 * 这里自定义的AuthCheck注解用于aoop切面编程，用于管理员增加、修改、删除用户前进行权限校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    /**
     * 必须有某个角色
     * @return
     */
    String mustRole() default "";

}
