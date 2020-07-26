package com.java.spring.formework.webmvc.servlet;

import com.java.spring.formework.annotation.SPController;
import com.java.spring.formework.annotation.SPRequestMapping;
import com.java.spring.formework.context.SPApplicationContext;
import com.java.spring.formework.webmvc.servlet.SPHandlerAdapter;
import com.java.spring.formework.webmvc.servlet.SPHandlerMapping;
import com.java.spring.formework.webmvc.servlet.SPModelAndView;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author holler
 * @date 2020-07-20 22:37
 */
@Slf4j
public class SPDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private SPApplicationContext context;


    private List<SPHandlerMapping> handlerMappings = new ArrayList<SPHandlerMapping>();
//
    private Map<SPHandlerMapping, SPHandlerAdapter> handlerAdapters = new HashMap<SPHandlerMapping,SPHandlerAdapter>();
//
    private List<SPViewResolver> viewResolvers = new ArrayList<SPViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatcher(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp)throws Exception {
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        SPHandlerMapping handler = getHandler(req);

        if(handler == null){
            processDispatchResult(req,resp,new SPModelAndView("404"));
            return;
        }

        //2、准备调用前的参数
        SPHandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        assert ha != null;
        SPModelAndView mv = ha.handle(req,resp,handler);

        //这一步才是真正的输出
        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, SPModelAndView mv) throws Exception{
        //把给我的ModelAndView变成一个HTML、OutputStream、json、freemark、veolcity
        //ContextType
        if(null == mv){return;}

        //如果ModelAndView不为null，怎么办？
        if(this.viewResolvers.isEmpty()){return;}

        for (SPViewResolver viewResolver : this.viewResolvers) {
            SPView view = viewResolver.resolveViewName(mv.getViewName(),null);
            view.render(mv.getModel(),req,resp);
            return;
        }
    }


    private SPHandlerAdapter getHandlerAdapter(SPHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        SPHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return ha;
        }
        return null;
    }


    private SPHandlerMapping getHandler(HttpServletRequest req){
        if(this.handlerMappings.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (SPHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            //如果没有匹配上继续下一个匹配
            if(!matcher.matches()){ continue; }

            return handler;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        context = new SPApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC 九大组件
        initStrategies(context);

    }

    //初始化策略
    protected void initStrategies(SPApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);


        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);
        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    //参数缓存器
    private void initFlashMapManager(SPApplicationContext context) {

    }

    //初始化视图转换器，必须实现
    private void initViewResolvers(SPApplicationContext context) {
        //拿到一个模板的存放目录

        String templateRoot = context.getConfig().getProperty("templateRoot");

        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new SPViewResolver(templateRoot));
        }


    }

    //初始化视图预处理器
    private void initRequestToViewNameTranslator(SPApplicationContext context) {

    }

    //初始化异常拦截器
    private void initHandlerExceptionResolvers(SPApplicationContext context) {

    }

    //初始化参数适配器，必须实现
    private void initHandlerAdapters(SPApplicationContext context) {
        //把一个request请求变成一个handler，参数都是字符串的，自动匹配到handler中的形参

        //要拿到handlerMapping才能干活，意味着有几个handlerMapping就有几个handlerMapping
        for (SPHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new SPHandlerAdapter());
        }

    }

    //handlerMapping，必须实现
    private void initHandlerMappings(SPApplicationContext context) {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanDefinitionNames) {
                Object bean = context.getBean(beanName);
                Class<?> clazz = bean.getClass();
                if (!clazz.isAnnotationPresent(SPController.class)){
                    continue;
                }
                String baseUrl = "";
                //获取Controller的url配置
                if(clazz.isAnnotationPresent(SPRequestMapping.class)){
                    SPRequestMapping requestMapping = clazz.getAnnotation(SPRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //获取Method的url配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {

                    //没有加RequestMapping注解的直接忽略
                    if(!method.isAnnotationPresent(SPRequestMapping.class)){ continue; }

                    //映射URL
                    SPRequestMapping requestMapping = method.getAnnotation(SPRequestMapping.class);
                    //  /demo/query

                    //  (//demo//query)

                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);

                    this.handlerMappings.add(new SPHandlerMapping(pattern,bean,method));
                    log.info("Mapped " + regex + "," + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //初始化模板处理器
    private void initThemeResolver(SPApplicationContext context) {

    }

    //初始化本地语言环境
    private void initLocaleResolver(SPApplicationContext context) {


    }

    //多文件上传的组件
    private void initMultipartResolver(SPApplicationContext context) {

    }

}
