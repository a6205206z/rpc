package com.uoko.rpc.example.services;

import com.uoko.rpc.framework.annotations.RPCMethod;
import com.uoko.rpc.framework.annotations.RPCService;

@RPCService(name = "HelloService", type = HelloService.class)
public class HelloServiceImpl implements HelloService {

	@Override
	@RPCMethod(name="hello")
	public String hello(String name) {
		return "Hello " + name;
	}
	
}
