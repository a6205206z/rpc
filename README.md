#RPC Framework on Java
一款轻量级的RPC框架，目前还在完善中。

##包含功能有：
1. 远程调用
2. 服务注册与发现

##服务端演示
###HelloService.java
```java
package com.uoko.rpc.example.services;

public interface HelloService {
	String hello(String name);
}
```

###HelloServiceImpl.java
```java
package com.uoko.rpc.example.services;

public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		return "Hello " + name;
	}
	
}
```

###RpcProvider.java
```java
package com.uoko.rpc.example.services;
import com.uoko.rpc.framework.RPCServiceHost;
import com.uoko.rpc.framework.RPCServiceRegistry;

public class RpcProvider {
	public static void main(String[] args) throws Exception{
		HelloService service = new HelloServiceImpl();
		
		//registry service
		RPCServiceRegistry serviceRegistry = new RPCServiceRegistry("127.0.0.1:2181", "/services/HelloService", 10000); 
		serviceRegistry.Register(service, "192.168.99.1:8080");
		
		//start host
		RPCServiceHost.export(service, 8080);
	}
}
```

##客户端演示
###HelloService.java
```java
package com.uoko.rpc.example.client;

public interface HelloService {
	String hello(String name);
}
```

###RpcConsumer.java
```java
package com.uoko.rpc.example.client;


import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.uoko.rpc.example.services.HelloService;
import com.uoko.rpc.framework.RPCClientProxy;
import com.uoko.rpc.framework.RPCServiceDiscovery;
import com.uoko.rpc.framework.RPCServiceDiscoveryHandler;

public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		
		//invoke
		HelloService service = RPCClientProxy.refer(HelloService.class, "127.0.0.1", 8080);
		String result = service.hello("Cean");
		System.out.println(result);
	}
}

```
