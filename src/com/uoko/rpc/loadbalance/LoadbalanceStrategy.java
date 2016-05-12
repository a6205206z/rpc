/*
 * {@code LoadbalanceStrategy}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.loadbalance;

import java.util.List;

public interface LoadbalanceStrategy {
	/*
	 * 
	 * 
	 * select one value from list
	 * 
	 * @param list
	 * 		from list
	 * 
	 * @return one value
	 * 
	 * 
	 * */
	<T> T selectOne(List<T> list);
}
