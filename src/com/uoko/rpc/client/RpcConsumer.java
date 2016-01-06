package com.uoko.rpc.client;


import com.uoko.rpc.framework.RPCClientProxy;

public class RpcConsumer {
	public static void main(String[] args) throws Exception{
		HelloService service = RPCClientProxy.refer(HelloService.class, "127.0.0.1", 8080);
		String result = service.hello("Cean");
		System.out.println(result);
	}
}
