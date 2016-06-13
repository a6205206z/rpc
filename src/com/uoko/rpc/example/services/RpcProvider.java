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
import com.uoko.rpc.provider.Exporter;
import com.uoko.rpc.provider.ExporterFactory;

public class RpcProvider {
	public static void main(String[] args) throws Exception{
		//step 1.  create service
		HelloService helloService = new HelloServiceImpl();
		UserService userService = new UserServiceImpl();
		
		Exporter exporter = ExporterFactory.getInstance().create();
		
		//step 2. addservice in
		exporter.AddService(HelloService.class,helloService, "1.0");
		exporter.AddService(UserService.class, userService, "1.0");
		
		
		//step 3. provide service
		exporter.export();
	}
}
