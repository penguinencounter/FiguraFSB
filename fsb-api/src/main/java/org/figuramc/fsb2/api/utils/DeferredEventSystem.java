package org.figuramc.fsb2.api.utils;


import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class DeferredEventSystem {
    /**
     * Implementer: this should queue the Runnable on the appropriate thread (render/server thread)
     */
    protected abstract void enqueue(Runnable action);

    public class EventBus<T extends ImmediateEventSystem.Event, C> {
        private final ImmediateEventSystem.EventBus<T, C> backend = new ImmediateEventSystem.EventBus<>();

        /**
         * Register an event handler. <b>Higher priority runs first.</b> Builtins are priority 0.
         */
        public void register(ImmediateEventSystem.FSBEventHandler<T, C> handler, int priority) {
            backend.register(handler, priority);
        }

        public void dispatch(T event, C context) {
            enqueue(() -> backend.dispatch(event, context));
        }
    }

    public class ReturnableEventBus<T extends ImmediateEventSystem.Event, C, R> {
        private final ImmediateEventSystem.ReturnableEventBus<T, C, R> backend = new ImmediateEventSystem.ReturnableEventBus<>();

        /**
         * Register an event handler. <b>Higher priority runs first.</b> Builtins are priority 0.
         */
        public void register(ImmediateEventSystem.FSBReturnableEventHandler<T, C, R> handler, int priority) {
            backend.register(handler, priority);
        }

        public CompletableFuture<@Nullable R> dispatch(T event, C context) {
            CompletableFuture<@Nullable R> fut = new CompletableFuture<>();
            enqueue(() -> fut.complete(backend.dispatch(event, context)));
            return fut;
        }
    }
}

