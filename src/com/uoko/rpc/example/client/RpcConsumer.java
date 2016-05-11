/*
 * {@code RpcConsumer}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.client;


import com.uoko.rpc.example.services.HelloService;
import com.uoko.rpc.proxy.ServiceProxy;

public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		
		//invoke
		ServiceProxy proxy = ServiceProxy.getInstance();
		HelloService service = proxy.refer(HelloService.class,"1.0");
		for(int i = 0;i < 100; ++i){
			String result = service.hello("Cean");
			System.out.println(result);
		}
	}
}
