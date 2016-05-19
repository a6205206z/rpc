/*
 * {@code HelloServiceImpl}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.services;

import com.uoko.rpc.example.interfaces.HelloService;
import com.uoko.rpc.example.interfaces.PersonEnttiy;

public class HelloServiceImpl implements HelloService {
	@Override
	public String hello(PersonEnttiy person) {
		return "Hello " + person.getName();
	}
	
}
