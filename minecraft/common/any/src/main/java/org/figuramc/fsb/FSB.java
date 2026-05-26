package org.figuramc.fsb;

import org.figuramc.fsb.internals.logging.LogService;
import org.figuramc.fsb.internals.logging.LoggingProxy;

public class FSB {
    public static final LoggingProxy LOGGER = LogService.getLogger();

    public static void init() {
        LOGGER.info("hello! from common/any");
    }
}
