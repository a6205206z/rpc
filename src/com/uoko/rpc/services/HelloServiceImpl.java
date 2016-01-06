package com.uoko.rpc.services;

public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		return "Hello " + name;
	}
	
}
