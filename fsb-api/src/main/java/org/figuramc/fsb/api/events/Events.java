package org.figuramc.fsb.api.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class Events {
    private static final HashMap<Class<? extends Event>, ArrayList<Handler<?>>> handlers = new HashMap<>();

    public static <T extends Event> void registerHandler(Class<T> eventClass, Handler<T> handler) {
        registerHandler(eventClass, handler, 0);
    }

    public static <T extends Event> void registerHandler(Class<T> eventClass, Handler<T> handler, int priority) {
        ArrayList<RegisteredHandler<T>> handlers = getHandlers(eventClass);
        handlers.add(new RegisteredHandler<>(priority, handler));
        handlers.sort(RegisteredHandler::compareTo);
    }

    public static <T extends Event> void removeHandler(Class<T> eventClass, Handler<T> handler) {
        getHandlers(eventClass).removeIf(r -> r.handler.equals(handler));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> T call(T event) {
        ArrayList<RegisteredHandler<T>> handlers = (ArrayList<RegisteredHandler<T>>) (Object) getHandlers(event.getClass());
        for (RegisteredHandler<T> regHandler: handlers) {
            regHandler.handler.handle(event);
            if (!event.canContinue()) break;
        }
        return event;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> ArrayList<RegisteredHandler<T>> getHandlers(Class<T> clazz) {
        return (ArrayList<RegisteredHandler<T>>) (Object) handlers.computeIfAbsent(clazz, k -> new ArrayList<>());
    }

    public interface Handler<E extends Event> {
        void handle(E event);
    }

    private static final class RegisteredHandler<T extends Event> implements Comparable<RegisteredHandler<T>> {
        private final int priority;
        private final Handler<T> handler;

        private RegisteredHandler(int priority, Handler<T> handler) {
            this.priority = priority;
            this.handler = handler;
        }

        @Override
        public int compareTo(RegisteredHandler<T> o) {
            return o.priority - priority;
        }

        public int priority() {
            return priority;
        }

        public Handler<T> handler() {
            return handler;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            RegisteredHandler that = (RegisteredHandler) obj;
            return this.priority == that.priority &&
                    Objects.equals(this.handler, that.handler);
        }

        @Override
        public int hashCode() {
            return Objects.hash(priority, handler);
        }

        @Override
        public String toString() {
            return "RegisteredHandler[" +
                    "priority=" + priority + ", " +
                    "handler=" + handler + ']';
        }

    }
}
