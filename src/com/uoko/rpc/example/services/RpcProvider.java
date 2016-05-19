/*
 * {@code RpcProvider}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.services;

import com.uoko.rpc.example.interfaces.HelloService;
import com.uoko.rpc.example.interfaces.UserService;
import com.uoko.rpc.protocol.Exporter;

public class RpcProvider {
	public static void main(String[] args) throws Exception{
		//step 1.  create service
		HelloService helloService = new HelloServiceImpl();
		UserService userService = new UserServiceImpl();
		
		//step 2. addservice in
		Exporter.getInstance().AddService(HelloService.class,helloService, "1.0");
		Exporter.getInstance().AddService(UserService.class, userService, "1.0");
		
		
		//step 3. provide service
		Exporter.getInstance().export();
	}
}
