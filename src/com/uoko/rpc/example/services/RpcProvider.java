/*
 * {@code RpcProvider}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.services;

import com.uoko.rpc.common.ProtocolOption;
import com.uoko.rpc.example.interfaces.HelloService;
import com.uoko.rpc.example.interfaces.UserService;
import com.uoko.rpc.provider.Exporter;
import com.uoko.rpc.provider.ExporterFactory;


public class RpcProvider {
	public static void main(String[] args) throws Exception{
		//step 1.  create service
		HelloService helloService = new HelloServiceImpl();
		UserService userService = new UserServiceImpl();
		
		//step 2. create expoter and select protocol
		Exporter exporter = ExporterFactory.getInstance().create("127.0.0.1",8080,ProtocolOption.simple);
		
		//step 3. add service in
		exporter.AddService(HelloService.class,helloService, "1.0");
		exporter.AddService(UserService.class, userService, "1.0");
		
		
		//step 4. provide service
		exporter.export();
	}
}
