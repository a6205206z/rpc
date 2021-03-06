/*
 * {@code ServiceDiscovery}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.discovery;

public interface ServiceDiscovery {
	/*
	 * 
	 * 
	 * 
	 * */
	<T> void beginDiscovery(final Class<T> interfaceClass,String version,ServiceDiscoveryHandler discoveryHandler);
	
	/*
	 * 
	 * 
	 * 
	 * */
	void endDiscovery();
}
