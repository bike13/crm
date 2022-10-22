package com.xxxx.crm.utils;

import java.lang.annotation.*;

//权限接口 方便后续设置权限  切面获取
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Power {
    String code() default "";
}

