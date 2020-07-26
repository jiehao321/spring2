package com.java.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 页面交互
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPController {
	String value() default "";
}
