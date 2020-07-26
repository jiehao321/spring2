package com.java.spring.formework.annotation;

import java.lang.annotation.*;


/**
 * 自动注入
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPAutowired {
	String value() default "";
}
