/*
 * {@code ServiceProxy}
 * 
 *
 *
 * @author      Cean Cheng
 * 
 * */

package com.uoko.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import com.uoko.rpc.cluster.Router;

public class ServiceProxy<T> implements InvocationHandler {
    private static final Logger logger = Logger.getLogger(ServiceProxy.class);

    private Class<T> interfaceClass;
    private Router<T> router;
    private Invoker<T> invoker;

    protected ServiceProxy(final Class<T> interfaceClass, String version) {
        this.interfaceClass = interfaceClass;
        this.router = new Router<>(interfaceClass, version);
        this.invoker = new SimpleInvoker<>(interfaceClass, version);
    }

    @SuppressWarnings("unchecked")
    public T refer() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String address = router.getServiceAddress();

        try {
            result = invoker.invoke(method, args, address, 2000);
        } catch (Exception e) {
            logger.error(e);
        }
        return result;
    }

    protected void close() {
        if (this.router != null) {
            this.router.close();
        }
        if (this.invoker != null) {
            this.invoker.dispose();
        }
    }
}
