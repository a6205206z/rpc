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

public class Transporter implements Serializable {
	private static final long serialVersionUID = 1L;

	private String transporterID;
	private MethodInfo methodInfo;
	private int statusCode;
	private String exceptionBody;
	
	public Transporter(MethodInfo methodInfo){
		this.transporterID = UUID.randomUUID().toString();
		this.methodInfo = methodInfo;
	}
	
	public String getTransporterID(){
		return transporterID;
	}

	public MethodInfo getMethodInfo() {
		return methodInfo;
	}
	

	public void setMethodInfo(MethodInfo methodInfo) {
		this.methodInfo = methodInfo;
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
	
}
