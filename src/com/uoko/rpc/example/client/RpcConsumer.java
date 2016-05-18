/*
 * {@code RpcConsumer}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.client;


import com.uoko.rpc.example.services.HelloService;
import com.uoko.rpc.example.services.PersonEnttiy;
import com.uoko.rpc.proxy.ProxyFactory;
import com.uoko.rpc.proxy.ServiceProxy;

public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		
		ServiceProxy<HelloService> proxy = ProxyFactory.getInstance().createProxy(HelloService.class,"1.0");
		HelloService service = proxy.refer();
		PersonEnttiy person = null;
		for(int i = 0;i < 100; ++i){
			person = new PersonEnttiy();
			person.setName("Cean Cheng");
			person.setSex("Male");
			person.setAge(10);
			String result = service.hello(person);
			System.out.println(result);
		}
		
		proxy.close();
	}
}
