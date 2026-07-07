package org.figuramc.fsb2.server;

import org.figuramc.fsb2.FSB;
import org.figuramc.fsb2.services.FSBInitializerService;

public class FSBServerInit implements FSBInitializerService {
    @Override
    public void init() {
        FSB.LOGGER.info("hi from serverinit");
    }

    @Override
    public int priority() {
        return 10;
    }
}
