package com.uoko.rpc.framework.serviceregistry;

public interface ServiceRegistry {
	/*
	 * @param interfaceClass
     *            service interface class
     * @param version
     *            version of service
     * @param serviceAddress
     * 			  address of remote service
     * 			  127.0.0.1:8080
     * 
	 * */
	<T> void Register(final Class<T> interfaceClass,String version,String serviceAddress);
}
