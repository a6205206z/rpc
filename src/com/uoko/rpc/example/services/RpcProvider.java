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
		
		//step 2. provide service
		Exporter.getInstance().export(service,"1.0","192.168.99.1",8080);
	}
}
