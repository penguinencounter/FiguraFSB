package org.figuramc.fsb2.server.internals;

import org.figuramc.fsb2.services.FSBNetworkingService;

import java.util.ServiceLoader;

public class NetworkingService {
    public static final FSBNetworkingService<?> SERVICE = getNetworkingService();

    private static FSBNetworkingService<?> getNetworkingService() {
        @SuppressWarnings("rawtypes")
        ServiceLoader<FSBNetworkingService> load = ServiceLoader.load(FSBNetworkingService.class);

        for (FSBNetworkingService<?> serv : load) {
            return serv;
        }
        throw new BindingException("no available networking service");
    }
}
