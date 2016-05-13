/*
 * {@code HelloService}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.services;

import com.uoko.rpc.annotations.RPCMethod;
import com.uoko.rpc.annotations.RPCService;

@RPCService(name = "HelloService", type = HelloService.class)
public interface HelloService {
	@RPCMethod(name="hello")
	String hello(PersonEnttiy person);
}
