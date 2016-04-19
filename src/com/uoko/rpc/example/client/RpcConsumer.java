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
		
		
		//discover
		CountDownLatch latch = new CountDownLatch(1);
		RPCServiceDiscovery serviceDiscovery = new RPCServiceDiscovery("127.0.0.1:2181", "/services", 10000, 
				new RPCServiceDiscoveryHandler(){
					@Override
					public void serviceChanged(List<String> serviceInfos) {
						for(String info:serviceInfos){
							System.out.println(info);
							latch.countDown();
						}
					}
		});
		serviceDiscovery.Init();
		latch.await();
	}
}
