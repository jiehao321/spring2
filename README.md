Spring分析之IOC、DI、AOP

1. IOC分析

ioc的核心方法是AbstractApplicationContext抽象类里面的refresh()方法，首先定位资源配置文件，然后加载配置文件、扫描相关类，将他们封装成为一个BeanDefinition,向ioc容器中注册。注册的关键方法是DefaultListableBeanFactory类下的registerBeanDefinition方法，传入参数是一个name和一个BeanDefinition对象。

    	//向IOC容器注册解析的BeanDefiniton
    	@Override
    	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
    			throws BeanDefinitionStoreException {
    		//......
            
    		BeanDefinition oldBeanDefinition;
    
    		oldBeanDefinition = this.beanDefinitionMap.get(beanName);
    
    		if (oldBeanDefinition != null) {
    			//.......
    			this.beanDefinitionMap.put(beanName, beanDefinition);
    		}
    		else {
    			if (hasBeanCreationStarted()) {
    				// Cannot modify startup-time collection elements anymore (for stable iteration)
    				//注册的过程中需要线程同步，以保证数据的一致性
    				synchronized (this.beanDefinitionMap) {
    					this.beanDefinitionMap.put(beanName, beanDefinition);
    					List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
    					updatedDefinitions.addAll(this.beanDefinitionNames);
    					updatedDefinitions.add(beanName);
    					this.beanDefinitionNames = updatedDefinitions;
    					if (this.manualSingletonNames.contains(beanName)) {
    						Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
    						updatedSingletons.remove(beanName);
    						this.manualSingletonNames = updatedSingletons;
    					}
    				}
    			}
    			else {
    				// Still in startup registration phase
    				this.beanDefinitionMap.put(beanName, beanDefinition);
    				this.beanDefinitionNames.add(beanName);
    				this.manualSingletonNames.remove(beanName);
    			}
    			this.frozenBeanDefinitionNames = null;
    		}
    
    		//检查是否有同名的BeanDefinition已经在IOC容器中注册
    		if (oldBeanDefinition != null || containsSingleton(beanName)) {
    			//重置所有已经注册过的BeanDefinition的缓存
    			resetBeanDefinition(beanName);
    		}
    	}

在简化版中，refresh()方法位如下：

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

DI分析

在spring中，正真开始进行依赖注入的入口方法是getBean(),但是他们都调用的是doGetBean()方法，在doGetBean()方法中，创建一个bean的方法是createBean()，但是在当前类是一个抽象方法，交给实现类AbstractAutowireCapableBeanFactory来实现。在createBean()方法中，真正创建bean的方法是doCreateBean()，首先创建bean的实例对象，方法是createBeanInstance()，默认是使用无参构造方法创建实例，接下来进行依赖注入，方法是populateBean()。然后进行属性注入，方法为applyPropertyValues()。

简化版如下：

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

MVC分析

首先容器加载，初始化init方法，是在HttpServletBean()方法里，这里调用了一个initServletBean();方法，采用委派器模式，交给子类FrameworkServlet来实现，在这里执行初始化initWebApplicationContext()方法，在这个方法中，触发mvc流程入口onRefresh(wac);方法，同牙膏也是委派器模式，交给子类DispatcherServlet实现，执行initStrategies(context);方法，初始化mvc九大组件，

    //初始化策略
    protected void initStrategies(ApplicationContext context) {
       //多文件上传的组件
       initMultipartResolver(context);
       //初始化本地语言环境
       initLocaleResolver(context);
       //初始化模板处理器
       initThemeResolver(context);
       //handlerMapping
       initHandlerMappings(context);
       //初始化参数适配器
       initHandlerAdapters(context);
       //初始化异常拦截器
       initHandlerExceptionResolvers(context);
       //初始化视图预处理器
       initRequestToViewNameTranslator(context);
       //初始化视图转换器
       initViewResolvers(context);
       //参数缓存器
       initFlashMapManager(context);
    }

用户请求都交给子类的DispatcherServlet实现，关键方法为doService()，然后doService()又调用doDispatch()方法处理，也就是中央控制器,控制请求的转发。在这里获取handler，然后根据handler去执行后端的业务逻辑，渲染等操作，返回请求。






