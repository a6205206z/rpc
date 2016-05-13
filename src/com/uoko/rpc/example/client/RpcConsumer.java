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
import com.uoko.rpc.proxy.ServiceProxy;

public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		
		//invoke
		ServiceProxy proxy = ServiceProxy.getInstance();
		HelloService service = proxy.refer(HelloService.class,"1.0");
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
