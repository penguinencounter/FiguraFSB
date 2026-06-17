package org.figuramc.fsb2.api.utils;

import java.util.concurrent.locks.Lock;

public final class Locking {
    public interface Resource extends AutoCloseable {
        /* delete checked exception */
        @Override
        void close();
    }

    public static Resource use(Lock l) {
        l.lock();
        return l::unlock;
    }
}
