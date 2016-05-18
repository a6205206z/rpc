package com.uoko.rpc.transport;

import java.io.Serializable;

public class ServiceInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String version;
	private String serviceName;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
