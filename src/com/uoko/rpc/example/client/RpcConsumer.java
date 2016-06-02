/*
 * {@code RpcConsumer}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.client;


import com.uoko.rpc.example.interfaces.HelloService;
import com.uoko.rpc.example.interfaces.PersonEnttiy;
import com.uoko.rpc.example.interfaces.UserService;
import com.uoko.rpc.proxy.ProxyFactory;
import com.uoko.rpc.proxy.ServiceProxy;

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
			for(int i=0;i<1000;i++){
				String result = helloService.hello(person);
				
				System.out.println(result);
			}
		}catch(Exception e){
			e.printStackTrace();
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
