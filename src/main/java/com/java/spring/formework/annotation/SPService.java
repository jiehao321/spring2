package com.java.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 业务逻辑,注入接口
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPService {
	String value() default "";
}
