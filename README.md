#RPC Framework on Java
一款轻量级的RPC框架，目前还在完善中。

##包含功能有：
###1. 远程调用
####1.1Provider演示
#####HelloService.java
```java
package com.uoko.rpc.example.services;

import com.uoko.rpc.framework.annotations.RPCMethod;
import com.uoko.rpc.framework.annotations.RPCService;

@RPCService(name = "HelloService", type = HelloService.class)
public interface HelloService {
	@RPCMethod(name="hello")
	String hello(String name);
}

```

#####HelloServiceImpl.java
```java
package com.uoko.rpc.example.services;

public class HelloServiceImpl implements HelloService {
	@Override
	public String hello(String name) {
		return "Hello " + name;
	}
	
}


```

#####RpcProvider.java
```java
package com.uoko.rpc.example.services;

import com.uoko.rpc.framework.RPCServiceHost;
import com.uoko.rpc.framework.RPCServiceRegistry;

public class RpcProvider {
	public static void main(String[] args) throws Exception{
		//step 1.  create service
		HelloService service = new HelloServiceImpl();
		
		//step 2. export service
		RPCServiceHost.export(service, 8080);
	}
}

```

####1.2Consumer演示
#####HelloService.java
```java
package com.uoko.rpc.example.client;

public interface HelloService {
	String hello(String name);
}
```

#####RpcConsumer.java
```java
package com.uoko.rpc.example.client;


import com.uoko.rpc.example.services.HelloService;
import com.uoko.rpc.framework.RPCClientProxy;

public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		
		//invoke
		/*
		*@parameters service class type , version, zk address,zk root path, zk timeout
		*/
		RPCClientProxy<HelloService> proxy = new RPCClientProxy<HelloService>(HelloService.class, "1.0", "127.0.0.1:2181", "/services", 10000);
		HelloService service = proxy.refer();
		for(int i = 0;i < 100; ++i){
			String result = service.hello("Cean");
			System.out.println(result);
		}
	}
}


```

###2. 服务注册与发现
####2.1服务注册  
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

###3. 软负载
目前支持以下负载策略  
####3.1随机策略  
####3.2轮询策略  
