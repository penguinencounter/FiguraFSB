package org.figuramc.fsb2.services;

public interface FSBInitializerService {
    void init();

    default int priority() {
        return 0;
    }
}
