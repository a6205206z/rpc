/*
 * {@code ServiceDiscoveryHandler}
 * 
 *
 *
 * @author      Cean Cheng
 * */


package com.uoko.rpc.discovery;

import java.util.List;

public interface ServiceDiscoveryHandler {
	/*
	 * 
	 * 
	 * when service registry info changed then do it.
	 * 
	 * @param serviceInfos
	 * 		from list
	 * 
	 * 
	 * 
	 * */
	abstract public void serviceChanged(List<String> serviceInfos);
}
