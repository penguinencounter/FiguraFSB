package org.figuramc.fsb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSB {
    public static final Logger LOGGER = LoggerFactory.getLogger(FSB.class);

    public static void init() {
        LOGGER.info("hello! from common/any");
    }
}
