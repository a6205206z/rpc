#RPC Framework on Java
一款轻量级的RPC框架，目前还在完善中。

##包含功能有：
###1. 远程调用
####1.1.Provider演示
#####HelloService.java
```java
@RPCService(name = "HelloService", type = HelloService.class)
public interface HelloService {
	@RPCMethod(name="hello")
	String hello(PersonEnttiy person);
}
```

#####HelloServiceImpl.java
```java
public class HelloServiceImpl implements HelloService {
	@Override
	public String hello(PersonEnttiy person) {
		return "Hello " + person.getName();
	}
	
}
```

#####PersonEnttiy.java
```java
public class PersonEnttiy implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String sex;
	private int age;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
```

#####RpcProvider.java
```java
public class RpcProvider {
	public static void main(String[] args) throws Exception{
		//step 1.  create service
		HelloService service = new HelloServiceImpl();
		
		//step 2. provide service
		ServiceProvider.provide(service,"1.0","192.168.99.1",8080);
	}
}
```

####1.2.Consumer演示
#####HelloService.java
```java
public interface HelloService {
	String hello(PersonEnttiy person);
}
```

#####RpcConsumer.java
```java
public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		
		//invoke
		ServiceProxy<HelloService> proxy = ProxyFactory.getInstance().createProxy(HelloService.class,"1.0");
		HelloService service = proxy.refer();
		PersonEnttiy person = null;
		
		person = new PersonEnttiy();
		person.setName("Cean Cheng");
		person.setSex("Male");
		person.setAge(10);
		String result = service.hello(person);
		System.out.println(result);

		
		proxy.close();
	}
}
```

#####PersonEnttiy.java
```java
public class PersonEnttiy implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String sex;
	private int age;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
```

###2. 服务注册与发现
####2.1.服务注册  
多注册中心  
zookeeper注册中心  
#####zookeeper注册中心配置实例  
######server.xml  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns="http://www.springframework.org/schema/beans"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
    <bean id="serviceRegistryFactory" class="com.uoko.rpc.framework.serviceregistry.ServiceRegistryFactory">
	<property name="loadServiceRegistry">
		<value>zookeeperServiceRegistry</value>
	</property>
    </bean> 
    <bean id="zookeeperServiceRegistry" class="com.uoko.rpc.framework.serviceregistry.ZookeeperServiceRegistry">
	<property name="zookeeper">
		<value>127.0.0.1:2181</value>
	</property>
	<property name="zookeeperRootPath">
		<value>/services</value>
	</property>
	<property name="sessionTimeout">
		<value>10000</value>
	</property>
    </bean> 
</beans>  
```
#####zookeeper服务发现配置实例  
######proxy.xml  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns="http://www.springframework.org/schema/beans"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
    <bean id="serviceDiscoveryFactory" class="com.uoko.rpc.discovery.ServiceDiscoveryFactory">
	<property name="loadServiceDiscovery">
		<value>zookeeperServiceDiscovery</value>
	</property>
    </bean> 
    <bean id="zookeeperServiceDiscovery" class="com.uoko.rpc.discovery.ZookeeperServiceDiscovery">
	<property name="zookeeper">
		<value>127.0.0.1:2181</value>
	</property>
	<property name="zookeeperRootPath">
		<value>/services</value>
	</property>
	<property name="sessionTimeout">
		<value>10000</value>
	</property>
    </bean> 
</beans>  
```
###3. 软负载
目前支持以下负载策略  
####3.1.随机策略  
####3.2.轮询策略  
