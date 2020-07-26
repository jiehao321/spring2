package com.java.spring.formework.beans;

/**
 * @author holler
 * @date 2020-07-19 14:47
 */
public class SPBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public SPBeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
