package com.java.spring.formework.aop.aspect;



import com.java.spring.formework.aop.intercept.SPMethodInterceptor;
import com.java.spring.formework.aop.intercept.SPMethodInvocation;

import java.lang.reflect.Method;


public class SPAfterThrowingAdviceInterceptor extends SPAbstractAspectAdvice implements SPAdvice, SPMethodInterceptor {


    private String throwingName;

    public SPAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(SPMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
