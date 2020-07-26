package com.java.spring.demo.action;


import com.java.spring.demo.service.IModifyService;
import com.java.spring.demo.service.IQueryService;
import com.java.spring.formework.annotation.SPAutowired;
import com.java.spring.formework.annotation.SPController;
import com.java.spring.formework.annotation.SPRequestMapping;
import com.java.spring.formework.annotation.SPRequestParam;
import com.java.spring.formework.webmvc.servlet.SPModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 *
 */
@SPController
@SPRequestMapping("/web")
public class MyAction {

	@SPAutowired
	IQueryService queryService;
	@SPAutowired
	IModifyService modifyService;

	@SPRequestMapping("/query.json")
	public SPModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@SPRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@SPRequestMapping("/add*.json")
	public SPModelAndView add(HttpServletRequest request,HttpServletResponse response,
			   @SPRequestParam("name") String name,@SPRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
//			e.printStackTrace();
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new SPModelAndView("500",model);
		}

	}
	
	@SPRequestMapping("/remove.json")
	public SPModelAndView remove(HttpServletRequest request,HttpServletResponse response,
		   @SPRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@SPRequestMapping("/edit.json")
	public SPModelAndView edit(HttpServletRequest request,HttpServletResponse response,
			@SPRequestParam("id") Integer id,
			@SPRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private SPModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
