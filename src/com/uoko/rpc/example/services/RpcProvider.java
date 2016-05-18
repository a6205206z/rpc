/*
 * {@code RpcProvider}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.services;

import com.uoko.rpc.protocol.Exporter;

public class RpcProvider {
	public static void main(String[] args) throws Exception{
		//step 1.  create service
		HelloService service = new HelloServiceImpl();
		
		//step 2. addservice in
		Exporter.getInstance().AddService(HelloService.class,service, "1.0");
		
		//step 3. provide service
		Exporter.getInstance().export();
	}
}
