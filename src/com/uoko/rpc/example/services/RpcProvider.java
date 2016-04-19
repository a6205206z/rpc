package com.uoko.rpc.example.services;

import com.uoko.rpc.framework.RPCServiceHost;
import com.uoko.rpc.framework.RPCServiceRegistry;

public class RpcProvider {
	public static void main(String[] args) throws Exception{
		//step 1.  create service
		HelloService service = new HelloServiceImpl();
		
		//step 2. create service registry
		RPCServiceRegistry serviceRegistry = RPCServiceRegistry.Create();
		
		//step 3. register service
		serviceRegistry.Register(service, "192.168.99.1:8080");
		
		//step 4. export service
		RPCServiceHost.export(service, 8080);
	}
}
