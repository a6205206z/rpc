/*
 * {@code Transporter}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.transport;

import java.io.Serializable;
import java.util.UUID;

public class Context implements Serializable {
	private static final long serialVersionUID = 1L;

	private String transporterID;
	private ServiceInfo service;
	private MethodInfo method;
	private int statusCode;
	private String exceptionBody;
	
	public Context(ServiceInfo service,MethodInfo method){
		this.transporterID = UUID.randomUUID().toString();
		this.service = service;
		this.method = method;
	}
	
	public String getTransporterID(){
		return transporterID;
	}

	public MethodInfo getMethod() {
		return method;
	}
	

	public void setMethod(MethodInfo methodInfo) {
		this.method = methodInfo;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getExceptionBody() {
		return exceptionBody;
	}

	public void setExceptionBody(String exceptionBody) {
		this.exceptionBody = exceptionBody;
	}

	public ServiceInfo getService() {
		return service;
	}

	public void setService(ServiceInfo service) {
		this.service = service;
	}
	
}
