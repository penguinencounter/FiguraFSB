package org.figuramc.fsb2.api.utils;


import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ImmediateEventSystem {
    public static class Event {
        private boolean stopped = false;

        public boolean stopped() {
            return stopped;
        }

        public void stopPropagation() {
            stopped = true;
        }
    }

    @FunctionalInterface
    public interface FSBEventHandler<T extends Event, C> {
        void act(T event, C context);
    }

    @FunctionalInterface
    public interface FSBReturnableEventHandler<T extends Event, C, R> {
        void act(T event, C context, AtomicReference<R> resultOut);
    }

    public static class EventBus<T extends Event, C> {
        private final ListMultimap<Integer, FSBEventHandler<T, C>> handlers = Multimaps.newListMultimap(
                new TreeMap<>(Collections.reverseOrder()), ArrayList::new
        );
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        /**
         * Register an event handler. <b>Higher priority runs first.</b> Builtins are priority 0.
         */
        public void register(FSBEventHandler<T, C> handler, int priority) {
            try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
                handlers.put(priority, handler);
            }
        }

        public void dispatch(T event, C context) {
            try (Locking.Resource ignored = Locking.use(lock.readLock())) {
                for (Collection<FSBEventHandler<T, C>> handlers : handlers.asMap().values()) {
                    for (FSBEventHandler<T, C> handler : handlers) {
                        handler.act(event, context);
                        if (event.stopped()) return;
                    }
                }
            }
        }
    }

    public static class ReturnableEventBus<T extends Event, C, R> {
        private final ListMultimap<Integer, FSBReturnableEventHandler<T, C, R>> handlers = Multimaps.newListMultimap(
                new TreeMap<>(Collections.reverseOrder()), ArrayList::new
        );
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        /**
         * Register an event handler. <b>Higher priority runs first.</b> Builtins are priority 0.
         */
        public void register(FSBReturnableEventHandler<T, C, R> handler, int priority) {
            try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
                handlers.put(priority, handler);
            }
        }

        public R dispatch(T event, C context) {
            AtomicReference<R> result = new AtomicReference<>(null);
            try (Locking.Resource ignored = Locking.use(lock.readLock())) {
                for (Collection<FSBReturnableEventHandler<T, C, R>> handlers : handlers.asMap().values()) {
                    for (FSBReturnableEventHandler<T, C, R> handler : handlers) {
                        handler.act(event, context, result);
                        if (event.stopped()) return result.get();
                    }
                }
            }
            return result.get();
        }
    }
}

