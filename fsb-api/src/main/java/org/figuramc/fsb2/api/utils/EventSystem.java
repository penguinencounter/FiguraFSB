package org.figuramc.fsb2.api.utils;


import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class EventSystem {
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
    public interface FSBEventHandler<T extends Event> {
        void act(T event);
    }

    @FunctionalInterface
    public interface FSBReturnableEventHandler<T extends Event, R> {
        void act(T event, AtomicReference<R> resultOut);
    }

    /**
     * Implementer: this should queue the Runnable on the appropriate thread (render/server thread)
     */
    protected abstract void enqueue(Runnable action);

    public class EventBus<T extends Event> {
        private final ListMultimap<Integer, FSBEventHandler<T>> handlers = Multimaps.newListMultimap(
                new TreeMap<>(Collections.reverseOrder()), ArrayList::new
        );
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        /**
         * Register an event handler. <b>Higher priority runs first.</b> Builtins are priority 0.
         */
        public void register(FSBEventHandler<T> handler, int priority) {
            try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
                handlers.put(priority, handler);
            }
        }

        private void dispatchInner(T event) {
            try (Locking.Resource ignored = Locking.use(lock.readLock())) {
                for (Collection<FSBEventHandler<T>> handlers : handlers.asMap().values()) {
                    for (FSBEventHandler<T> handler : handlers) {
                        handler.act(event);
                        if (event.stopped()) return;
                    }
                }
            }
        }

        public void dispatch(T event) {
            enqueue(() -> dispatchInner(event));
        }
    }

    public class ReturnableEventBus<T extends Event, R> {
        private final ListMultimap<Integer, FSBReturnableEventHandler<T, R>> handlers = Multimaps.newListMultimap(
                new TreeMap<>(Collections.reverseOrder()), ArrayList::new
        );
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        /**
         * Register an event handler. <b>Higher priority runs first.</b> Builtins are priority 0.
         */
        public void register(FSBReturnableEventHandler<T, R> handler, int priority) {
            try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
                handlers.put(priority, handler);
            }
        }

        private R dispatchInner(T event) {
            AtomicReference<R> result = new AtomicReference<>(null);
            try (Locking.Resource ignored = Locking.use(lock.readLock())) {
                for (Collection<FSBReturnableEventHandler<T, R>> handlers : handlers.asMap().values()) {
                    for (FSBReturnableEventHandler<T, R> handler : handlers) {
                        handler.act(event, result);
                        if (event.stopped()) return result.get();
                    }
                }
            }
            return result.get();
        }

        public CompletableFuture<R> dispatch(T event) {
            CompletableFuture<R> fut = new CompletableFuture<>();
            enqueue(() -> fut.complete(dispatchInner(event)));
            return fut;
        }
    }
}

