package com.uoko.rpc.proxy;

import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * com.uoko.rpc.proxy.ServiceProxyPool
 *
 * @author cheng.cheng
 * @version 1.0.1
 * @corporation 马上消费
 * @date 2017/11/14 14:07
 * @description
 */
public class ServiceProxyPool {
    private static final Logger logger = Logger.getLogger(ServiceProxyPool.class);

    private ConcurrentHashMap<String, ServiceProxy<?>> existServiceProxy;

    protected ServiceProxyPool() {
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> ServiceProxy<T> getOrCreateServiceProxy(final Class<T> interfaceClass, String version) {

        if (existServiceProxy == null) {
            existServiceProxy = new ConcurrentHashMap<>();
        }

        ServiceProxy<?> proxy = existServiceProxy.get(generateServiceProxyKey(interfaceClass.getName(), version));
        if (proxy == null) {
            existServiceProxy.put(generateServiceProxyKey(interfaceClass.getName(), version), new ServiceProxy<>(interfaceClass, version));
            proxy = existServiceProxy.get(generateServiceProxyKey(interfaceClass.getName(), version));
        }

        return (ServiceProxy<T>) proxy;
    }

    public synchronized void closeServiceProxy(final Class interfaceClass, String version){
        ServiceProxy<?> proxy = existServiceProxy.get(generateServiceProxyKey(interfaceClass.getName(), version));
        if(proxy != null){
            proxy.close();
        }
        existServiceProxy.remove(generateServiceProxyKey(interfaceClass.getName(), version));
    }

    private String generateServiceProxyKey(String serviceName, String version) {
        if (serviceName == null) {
            logger.error("serviceName == null");
            throw new IllegalArgumentException("serviceName == null");
        }
        if (version == null) {
            logger.error("version == null");
            throw new IllegalArgumentException("version == null");
        }

        return serviceName + version;
    }
}
