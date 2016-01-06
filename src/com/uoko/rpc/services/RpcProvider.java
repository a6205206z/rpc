package com.uoko.rpc.services;
import com.uoko.rpc.framework.RPCServiceHost;

public class RpcProvider {
	public static void main(String[] args) throws Exception{
		HelloService service = new HelloServiceImpl();
		RPCServiceHost.export(service, 8080);
	}
}
