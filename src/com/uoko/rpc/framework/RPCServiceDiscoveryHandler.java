/*
 * {@code RPCServiceDiscoveryHandler}
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.framework;

import java.util.List;

public interface RPCServiceDiscoveryHandler {
    abstract public void serviceChanged(List<String> serviceInfos);
}
