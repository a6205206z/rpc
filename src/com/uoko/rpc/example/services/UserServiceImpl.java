/*
 * {@code UserServiceImpl}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.example.services;

import com.uoko.rpc.example.interfaces.PersonEnttiy;
import com.uoko.rpc.example.interfaces.UserService;

public class UserServiceImpl 
	implements UserService {

	@Override
	public PersonEnttiy getOnePerson() {
		PersonEnttiy person = new PersonEnttiy();
		person.setName("cean cheng");
		person.setSex("Male");
		person.setAge(28);
		return person;
	}

}
