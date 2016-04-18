package com.uoko.rpc.services;
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
