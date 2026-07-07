package org.figuramc.fsb2;

import org.figuramc.fsb2.internals.InitializerService;
import org.figuramc.fsb2.internals.logging.LogService;
import org.figuramc.fsb2.internals.logging.LoggingProxy;

public class FSB {
    public static final LoggingProxy LOGGER = LogService.getLogger();

    public static void init() {
        // try to inline the indy?
        LOGGER.info("hello! from common/any");
        InitializerService.runInitializers();
    }
}
