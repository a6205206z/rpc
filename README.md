#RPC Framework on Java
一款轻量级的RPC框架，目前还在完善中。

##包含功能有：
###1. 远程调用(完成100%)
####1.1.统一接口
#####HelloService.java
```java
@RPCService(name = "HelloService", type = HelloService.class)
public interface HelloService {
	@RPCMethod(name="hello")
	String hello(PersonEnttiy person);
}
```
#####UserService.java
```java
@RPCService(name = "UserService", type = UserService.class)
public interface UserService {
	@RPCMethod(name="hello")
	PersonEnttiy getOnePerson();
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
####1.2.Provider演示

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
		HelloService helloService = new HelloServiceImpl();
		UserService userService = new UserServiceImpl();
		
		//step 2. create expoter and select protocol
		Exporter exporter = ExporterFactory.getInstance().create("127.0.0.1",8080,ProtocolOption.simple);
		
		//step 3. addservice in
		exporter.AddService(HelloService.class,helloService, "1.0");
		exporter.AddService(UserService.class, userService, "1.0");
		
		
		//step 4. provide service
		exporter.export();
	}
}
```

####1.3.Consumer演示
#####RpcConsumer.java
```java
public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		
		
		ServiceProxy<HelloService> helloServiceProxy = ProxyFactory.getInstance().createProxy(HelloService.class,"1.0");
		try{
			HelloService helloService = helloServiceProxy.refer();
			PersonEnttiy person = null;
			
			person = new PersonEnttiy();
			person.setName("Cean Cheng");
			person.setSex("Male");
			person.setAge(10);
			String result = helloService.hello(person);
			System.out.println(result);
		}catch(Exception e){
			
		}finally{
			helloServiceProxy.close();
		}
		
		
		
		ServiceProxy<UserService> userServiceProxy = ProxyFactory.getInstance().createProxy(UserService.class, "1.0");
		try{
			UserService userService = userServiceProxy.refer();
			PersonEnttiy person = userService.getOnePerson();
			
			System.out.println(person.getSex());
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			userServiceProxy.close();
		}
	}
}
```

###2. 服务注册与发现(完成60%)
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
    <bean id="serviceRegistryFactory" class="com.uoko.rpc.registry.ServiceRegistryFactory">
	    <property name="loadServiceRegistry">
			<value>zookeeperServiceRegistry</value>
		</property>
    </bean> 
    <bean id="zookeeperServiceRegistry" class="com.uoko.rpc.registry.ZookeeperServiceRegistry">
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
###3. 软负载(完成100%)
目前支持以下负载策略  
####3.1.随机策略  
####3.2.轮询策略

###4. 支持TCP长连接(完成100%)

###5. 多种协议选择(完成30%)
