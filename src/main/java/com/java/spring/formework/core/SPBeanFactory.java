package com.java.spring.formework.core;

/**
 * 单例工厂的顶层设计
 * @author holler
 * @date 2020-07-19 10:32
 */
public interface SPBeanFactory {

    /**
     * 根据beanName从IOC容器中获取一个实例bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws Exception;
    public Object getBean(Class<?> beanClass) throws Exception;
}
