package com.java;

import com.java.spring.demo.action.MyAction;
import com.java.spring.demo.service.IQueryService;
import com.java.spring.demo.service.impl.QueryService;
import com.java.spring.formework.context.SPApplicationContext;

import javax.management.Query;

/**
 * @author holler
 * @date 2020-07-19 16:18
 */
public class Test {

    public static void main(String[] args) throws Exception {
        SPApplicationContext context = new SPApplicationContext("classpath:application.properties");
        Object myAction = context.getBean(IQueryService.class);
        System.out.println(myAction);
    }
}
