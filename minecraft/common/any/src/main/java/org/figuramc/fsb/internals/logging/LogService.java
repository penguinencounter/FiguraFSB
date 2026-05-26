package org.figuramc.fsb.internals.logging;

import org.figuramc.fsb.internals.BindingException;
import org.figuramc.fsb.services.FSBLoggingService;

import java.util.ServiceLoader;

public final class LogService {
    public static LoggingProxy getLogger() {
        ServiceLoader<FSBLoggingService> load = ServiceLoader.load(FSBLoggingService.class);
        for (FSBLoggingService serv : load) {
            return new LoggingProxy(serv.getLogger());
        }
        throw new BindingException("no available logging service");
    }
}
