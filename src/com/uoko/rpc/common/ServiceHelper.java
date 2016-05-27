package com.uoko.rpc.common;

import org.apache.log4j.Logger;


public class ServiceHelper {
	private static final Logger logger = Logger.getLogger(ServiceHelper.class);
	
	public static String generateServiceInvokersKey(String serviceName,String version){
		if(serviceName == null){
			logger.error("serviceName == null");
			throw new IllegalArgumentException("serviceName == null");
		}
		if(version == null){
			logger.error("version == null");
			throw new IllegalArgumentException("version == null");
		}
		
		return serviceName + version;
	}
}
