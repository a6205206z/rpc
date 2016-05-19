/*
 * {@code UserService}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.interfaces;

import com.uoko.rpc.annotations.RPCMethod;
import com.uoko.rpc.annotations.RPCService;

@RPCService(name = "UserService", type = UserService.class)
public interface UserService {
	@RPCMethod(name="hello")
	PersonEnttiy getOnePerson();
}
