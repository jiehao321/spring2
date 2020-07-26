package com.java.spring.formework.context;

import com.java.spring.formework.annotation.SPAutowired;
import com.java.spring.formework.annotation.SPController;
import com.java.spring.formework.annotation.SPService;
import com.java.spring.formework.core.SPBeanFactory;
import com.java.spring.formework.beans.SPBeanWrapper;
import com.java.spring.formework.beans.config.SPBeanDefinition;
import com.java.spring.formework.beans.config.SPBeanPostProcessor;
import com.java.spring.formework.beans.support.SPBeanDefinitionReader;
import com.java.spring.formework.beans.support.SPDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 原生中这里是一个接口，有xml、注解等多个实现类
 * @author holler
 * @date 2020-07-19 10:38
 */
public class SPApplicationContext extends SPDefaultListableBeanFactory implements SPBeanFactory {

    private String [] configLocations;
    private SPBeanDefinitionReader reader;

    //单例的ioc容器缓存
    private Map<String, Object> singletonObject = new ConcurrentHashMap<String, Object>();

    //通用的ioc容器
    private Map<String, SPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, SPBeanWrapper>();

    public SPApplicationContext(String... configLocations){
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh(){
        //1、定位配置文件
         reader = new SPBeanDefinitionReader(this.configLocations);

        //2、加载配置文件、扫描相关的类、把他们封装成BeanDefinition
        List<SPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3、注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);

        //4、把不是延时加载的类，提前初始化
        doAutowired();
    }

    //只处理非延时加载的情况
    private void doAutowired() {
        for (Map.Entry<String, SPBeanDefinition> stringSPBeanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = stringSPBeanDefinitionEntry.getKey();
            if (!stringSPBeanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doRegisterBeanDefinition(List<SPBeanDefinition> beanDefinitions) {
        for (SPBeanDefinition beanDefinition:beanDefinitions) {
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }

    }

    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    public Object getBean(String beanName) throws Exception {
        //spring核心依赖注入
        //解决循环依赖 a中注入b，b中注入a,  先放在把所有的实例初始化放在一个容器里，然后在进行依赖注入
        //1、初始化

        SPBeanDefinition spBeanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式
        SPBeanPostProcessor postProcessor = new SPBeanPostProcessor();
        SPBeanDefinition beanDefinition = new SPBeanDefinition();
        postProcessor.postProcessBeforeInitialization(instance,beanName);

        instance = instantiateBean(beanName,spBeanDefinition);

        //3、把这个对象封装到BeanWrapper中
        SPBeanWrapper beanWrapper = new SPBeanWrapper(instance);

        //拿到beanWrapper之后，将beanWrapper保存到ioc容器中去
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);
        postProcessor.postProcessAfterInitialization(instance,beanName);
        //2、注入
        populateBean(beanName, new SPBeanDefinition(), beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    //注入关键方法
    private void populateBean(String beanName, SPBeanDefinition spBeanDefinition, SPBeanWrapper spBeanWrapper) {
        //获取beanWrapped中的实例
        Object instance = spBeanWrapper.getWrappedInstance();
        Class<?> clazz = spBeanWrapper.getWrappedClass();
        //判断只有加了注解的类才可以执行依赖注入
        if (!(clazz.isAnnotationPresent(SPController.class) || clazz.isAnnotationPresent(SPService.class))){
            return;
        }

        //获取所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field :fields
                ) {
            if (!field.isAnnotationPresent(SPAutowired.class)){
                continue;
            }
            SPAutowired autowired = field.getAnnotation(SPAutowired.class);

            String autowiredName = autowired.value().trim();
            if ("".equals(autowiredName)) {
                autowiredName = field.getType().getName();
            }
            //强制访问
            field.setAccessible(true);
            try {
                //为什么会为NULL，先留个坑
                if(this.factoryBeanInstanceCache.get(autowiredName) == null){ continue; }
//                if(instance == null){
//                    continue;
//                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //初始化关键方法
    private Object instantiateBean(String beanName, SPBeanDefinition spBeanDefinition) {
        //1、拿到要实例化的对象的类名
        String beanClassName = spBeanDefinition.getBeanClassName();
        //2、反射、实例化，得到对象
        Object instance = null;
        try {
            //对象存入ioc容器
            //默认单例
            if (this.singletonObject.containsKey(beanClassName)){
                instance = this.singletonObject.get(beanClassName);
            }else {
                Class<?> aClass = Class.forName(beanClassName);
                instance = aClass.newInstance();
                this.singletonObject.put(beanClassName, instance);
                this.singletonObject.put(spBeanDefinition.getFactoryBeanName(), instance);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet()
                .toArray(new  String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
