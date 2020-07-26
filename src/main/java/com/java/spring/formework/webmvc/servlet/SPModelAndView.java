package com.java.spring.formework.webmvc.servlet;

import java.util.Map;

/**
 * @author holler
 * @date 2020-07-21 22:54
 */
public class SPModelAndView {
    private String viewName;
    private Map<String,?> model;

    public SPModelAndView(String viewName) { this.viewName = viewName; }

    public SPModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

//    public void setViewName(String viewName) {
//        this.viewName = viewName;
//    }

    public Map<String, ?> getModel() {
        return model;
    }
}
