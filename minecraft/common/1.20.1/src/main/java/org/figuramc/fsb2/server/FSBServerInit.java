package org.figuramc.fsb2.server;

import org.figuramc.fsb2.services.FSBInitializerService;

public class FSBServerInit implements FSBInitializerService {
    @Override
    public void init() {
        FSB.LOGGER.info("fsb: target 1.21.4 having mixins");
    }

    @Override
    public int priority() {
        return 10;
    }
}
