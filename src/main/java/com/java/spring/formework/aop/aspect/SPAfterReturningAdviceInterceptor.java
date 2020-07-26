package com.java.spring.formework.aop.aspect;



import com.java.spring.formework.aop.intercept.SPMethodInterceptor;
import com.java.spring.formework.aop.intercept.SPMethodInvocation;

import java.lang.reflect.Method;


public class SPAfterReturningAdviceInterceptor extends SPAbstractAspectAdvice implements SPAdvice, SPMethodInterceptor {

    private SPJoinPoint joinPoint;

    public SPAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(SPMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
