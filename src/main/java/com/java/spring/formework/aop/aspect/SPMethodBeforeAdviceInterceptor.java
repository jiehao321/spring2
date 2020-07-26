package com.java.spring.formework.aop.aspect;



import com.java.spring.formework.aop.intercept.SPMethodInterceptor;
import com.java.spring.formework.aop.intercept.SPMethodInvocation;

import java.lang.reflect.Method;


public class SPMethodBeforeAdviceInterceptor extends SPAbstractAspectAdvice implements SPAdvice, SPMethodInterceptor {


    private SPJoinPoint joinPoint;
    public SPMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }
    @Override
    public Object invoke(SPMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
