package org.figuramc.fsb2.service_impl;

import org.figuramc.fsb2.FSB;
import org.figuramc.fsb2.services.FSBLoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSBLogging implements FSBLoggingService {
    @SuppressWarnings("LoggerInitializedWithForeignClass")
    public static final Logger LOGGER = LoggerFactory.getLogger(FSB.class);

    @Override
    public Object getLogger() {
        return LOGGER;
    }
}
