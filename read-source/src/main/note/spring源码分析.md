---
typora-copy-images-to: 资源
---

## spring源码分析

### 入口代码准备：

**spring-beans.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd">

<bean id="simpleBean" class="org.springframework.SimpleBean"/>

</beans>
```

**创建启动入口类**

```java
public class SpringCodeReader {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		SimpleBean simpleBean = context.getBean("simpleBean", SimpleBean.class);
		simpleBean.test();
		System.out.println("Hello world!");
	}
}
```

**SimpleBean**

```java
public class SimpleBean {

	public void test(){
		System.out.println("welcome to spring code world!");
	}
}
```

### ClassPathXmlApplicationContext

-----
![ClassPathXmlApplicationContext继承关系.png](images%2FClassPathXmlApplicationContext%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.png)

**ResourceLoader**是加载资源的接口，是明显的策略设计模式

**classPathXmlApplicationContext构造方法**

```java
	/**
	 * 创建新的 ClassPathXmlApplicationContext，从给定的 XML 文件加载定义并自动刷新上下文。
	 * Create a new ClassPathXmlApplicationContext, loading the definitions
	 * from the given XML file and automatically refreshing the context.
	 * @param configLocation resource location
	 * @throws BeansException if context creation failed
	 */
	public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}

	/**
	 * Create a new ClassPathXmlApplicationContext with the given parent,
	 * loading the definitions from the given XML files.
	 * @param configLocations array of resource locations
	 * @param refresh whether to automatically refresh the context,
	 * loading all bean definitions and creating all singletons.
	 * Alternatively, call refresh manually after further configuring the context.
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 * @see #refresh()
	 */
	public ClassPathXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {
		// parent is null
		super(parent);
        // 设置xml资源路径
		setConfigLocations(configLocations);
		if (refresh) {
			refresh();
		}
	}
```

​	这边调了super(parent)，所以要找父类的构造方法,根据继承关系一直，找到AbstractApplicationContext

```java
	/**
	 * Create a new AbstractApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractApplicationContext(@Nullable ApplicationContext parent) {
		this();
		setParent(parent);
	}
	/**
	 * Create a new AbstractApplicationContext with no parent.
	 */
	public AbstractApplicationContext() {
		this.resourcePatternResolver = getResourcePatternResolver();
	}
```

​	这里在AbstractApplicationContext的构造器中getResourcePatternResolver()生成ResourcePatternResolver（资源模式解析器），生成的解析器类型是PathMatchingResourcePatternResolver(一种支持Ant风格的解析器)

```java
	protected ResourcePatternResolver getResourcePatternResolver() {
		return new PathMatchingResourcePatternResolver(this);
	}
```

​	设置xml资源路径，即调用AbstractRefreshableConfigApplicationContext#setConfigLocations

```java
	/**
	* 这里支持传入多个路径
	*/
	public void setConfigLocations(@Nullable String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}
		else {
			this.configLocations = null;
		}
	}
```

​	调用resolvePath解析路径，如果路径中有占位符，会将占位符替换成对应的系统属性

```java
	protected String resolvePath(String path) {
        // 先获取环境对象，然后替换文件路径中的占位符
		return getEnvironment().resolveRequiredPlaceholders(path);
	}
```

​	getEnvironment()来获取环境对象，如果内存中没有，会创建

```java
	public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			this.environment = createEnvironment();
		}
		return this.environment;
	}
	
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardEnvironment();
	}
```

#### Enviroment

----
![Environment继承关系.png](images%2FEnvironment%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.png)


