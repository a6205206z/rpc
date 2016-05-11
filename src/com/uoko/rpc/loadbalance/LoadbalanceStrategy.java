package com.uoko.rpc.loadbalance;

import java.util.List;

public interface LoadbalanceStrategy {
	/*
	 * 
	 * 
	 * select one
	 * 
	 * 
	 * */
	<T> T selectOne(List<T> list);
}
