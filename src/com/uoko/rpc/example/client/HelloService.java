/*
 * {@code HelloService}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.client;

import com.uoko.rpc.example.services.PersonEnttiy;

public interface HelloService {
	String hello(PersonEnttiy person);
}
