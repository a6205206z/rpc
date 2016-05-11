package com.uoko.rpc.discovery;

import java.util.List;

public interface ServiceDiscoveryHandler {
	/*
	 * 
	 * 
	 * 
	 * 
	 * */
	abstract public void serviceChanged(List<String> serviceInfos);
}
