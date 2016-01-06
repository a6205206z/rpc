package com.uoko.rpc.framework;

import java.io.Serializable;

public class RPCTransferBean implements Serializable {
	private static final long serialVersionUID = 1510326612440404416L;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
}
