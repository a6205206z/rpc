/*
 * {@code ServiceProxy}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.uoko.rpc.cluster.Router;

public class ServiceProxy<T> {
	private static final Logger logger = Logger.getLogger(ServiceProxy.class);
	 
	private Class<T> interfaceClass;
	private String version;
	private Router<T> router;
	
	private static ConcurrentHashMap<String,ServiceProxy<?>> existInstances;
	
	
	private ServiceProxy(final Class<T> interfaceClass,String version){
		this.interfaceClass = interfaceClass;
		this.version = version;
		this.router = new Router<T>(interfaceClass,version);
	}
	
	/*
	 * 
	 * 
	 * 
	 * get instance each intetface
	 * 
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static synchronized <T> ServiceProxy<T> getInstanceEachInterface(final Class<T> interfaceClass,String version){
		
		if(existInstances == null){
			existInstances = new ConcurrentHashMap<String,ServiceProxy<?>>();
		}
		
		ServiceProxy<?> proxy = existInstances.get(generateServiceProxysKey(interfaceClass.getName(),version));
		if(proxy == null){
			existInstances.put(generateServiceProxysKey(interfaceClass.getName(),version), new ServiceProxy<T>(interfaceClass,version));
			proxy = existInstances.get(generateServiceProxysKey(interfaceClass.getName(),version));
		}
		
		return (ServiceProxy<T>)proxy;
	}
	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public T refer() throws Exception {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler(){
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String address = router.getServiceAddress();
				Invoker<T> invoker = new Invoker<T>(interfaceClass, version);
				return invoker.invoke(method, args, address);
			}
		});
	}


	public void close() {
		if(router != null){
			router.close();
		}
	}

	
	private static String generateServiceProxysKey(String serviceName,String version){
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
