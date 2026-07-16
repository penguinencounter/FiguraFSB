package org.figuramc.fsb2.server.internals;

import org.figuramc.fsb2.services.FSBInitializerService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ServiceLoader;

public class InitializerService {
    public static void runInitializers() {
        ServiceLoader<FSBInitializerService> load = ServiceLoader.load(FSBInitializerService.class);
        ArrayList<FSBInitializerService> collection = new ArrayList<>();
        load.forEach(collection::add);
        collection.sort(Comparator.comparingInt(FSBInitializerService::priority).reversed());
        collection.forEach(FSBInitializerService::init);
    }
}
