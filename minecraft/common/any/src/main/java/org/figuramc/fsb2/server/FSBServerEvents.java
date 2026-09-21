package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.packets.transfer.TransferOpenPacket;
import org.figuramc.fsb2.api.utils.EventSystem;

import java.util.function.Consumer;

public class FSBServerEvents extends EventSystem {
    /**
     * This field exists <b>purely as a helper for addon developers.</b> By default, events run on the network thread.
     * Before blocking on the main thread, make sure you are not on the main thread already.
     */
    public final Consumer<Runnable> runOnMainThread;

    public FSBServerEvents(Consumer<Runnable> runOnMainThread) {
        super();
        this.runOnMainThread = runOnMainThread;
    }

    @Override
    protected void enqueue(Runnable action) {
        action.run();
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
