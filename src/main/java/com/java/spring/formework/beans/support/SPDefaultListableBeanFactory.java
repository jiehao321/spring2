package com.java.spring.formework.beans.support;

import com.java.spring.formework.beans.config.SPBeanDefinition;
import com.java.spring.formework.context.support.SPAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author holler
 * @date 2020-07-19 10:48
 */
public class SPDefaultListableBeanFactory extends SPAbstractApplicationContext {

    //存储注册信息的BeanDefinition
    protected final Map<String, SPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, SPBeanDefinition>();




}
