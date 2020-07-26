package com.java.spring.formework.context.support;

/**
 * IOC容器实现的顶层设计
 * @author holler
 * @date 2020-07-19 10:42
 */
public abstract class SPAbstractApplicationContext {

    //受保护的，只提供给子类去重写
    public void refresh(){}


}
