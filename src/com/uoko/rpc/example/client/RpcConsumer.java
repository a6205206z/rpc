/*
 * {@code RpcConsumer}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.client;


import com.uoko.rpc.example.services.HelloService;
import com.uoko.rpc.framework.RPCClientProxy;

public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		
		//invoke
		RPCClientProxy<HelloService> proxy = new RPCClientProxy<HelloService>(HelloService.class, "1.0", "127.0.0.1:2181", "/services", 10000);
		HelloService service = proxy.refer();
		for(int i = 0;i < 100; ++i){
			String result = service.hello("Cean");
			System.out.println(result);
		}
	}
}
