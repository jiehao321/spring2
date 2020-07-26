package com.java.spring.formework.aop.intercept;


public interface SPMethodInterceptor {
    Object invoke(SPMethodInvocation invocation) throws Throwable;
}
