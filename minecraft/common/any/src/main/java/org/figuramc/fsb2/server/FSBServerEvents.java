package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.packets.transfer.TransferOpenPacket;
import org.figuramc.fsb2.api.utils.EventSystem;

import java.util.function.Consumer;

public class FSBServerEvents extends EventSystem {
    private final Consumer<Runnable> queuer;
    public FSBServerEvents(Consumer<Runnable> queuer) {
        super();
        this.queuer = queuer;
    }

    @Override
    protected void enqueue(Runnable action) {
        queuer.accept(action);
    }

    public static class TransferParametersEvent extends Event {
        public final TransferOpenPacket parameters;
        public final Object connection;

        public TransferParametersEvent(TransferOpenPacket parameters, Object connection) {
            this.parameters = parameters;
            this.connection = connection;
        }
    }

    public ReturnableEventBus<TransferParametersEvent, ServerSession, Boolean> BEFORE_TRANSFER = new ReturnableEventBus<>();
}
