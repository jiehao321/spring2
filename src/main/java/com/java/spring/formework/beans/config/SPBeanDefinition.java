package com.java.spring.formework.beans.config;

import lombok.Data;

/**
 * @author holler
 * @date 2020-07-19 10:52
 */
@Data
public class SPBeanDefinition {

    private String beanClassName;
    private final boolean lazyInit = false;
    private String factoryBeanName;
    private boolean isSingleton = true;

}
