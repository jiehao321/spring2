package com.java.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 请求url
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPRequestMapping {
	String value() default "";
}
