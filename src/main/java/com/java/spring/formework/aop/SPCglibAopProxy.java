package com.java.spring.formework.aop;


import com.java.spring.formework.aop.support.SPAdvisedSupport;


public class SPCglibAopProxy implements SPAopProxy {
    public SPCglibAopProxy(SPAdvisedSupport config) {
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
