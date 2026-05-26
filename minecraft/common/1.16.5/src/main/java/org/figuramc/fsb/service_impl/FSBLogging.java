package org.figuramc.fsb.service_impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.figuramc.fsb.FSB;
import org.figuramc.fsb.services.FSBLoggingService;

public class FSBLogging implements FSBLoggingService {
    @SuppressWarnings("LoggerInitializedWithForeignClass")
    public static final Logger LOGGER = LogManager.getLogger(FSB.class);

    @Override
    public Object getLogger() {
        return LOGGER;
    }
}
