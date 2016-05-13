/*
 * {@code HelloServiceImpl}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.services;

public class HelloServiceImpl implements HelloService {
	@Override
	public String hello(PersonEnttiy person) {
		return "Hello " + person.getName();
	}
	
}
