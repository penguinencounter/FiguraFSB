package org.figuramc.fsb2.services;

import org.figuramc.fsb2.api.packets.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface FSBNetworkingService<C> {
    void send(@NotNull C connection, @NotNull Packet<?> packet);

    void sendToPlayer(Object minecraftServer, @NotNull UUID player, @NotNull Packet<?> packet);

    /**
     * {@link #send} minus the {@link C} type variable
     * <p>
     * some situations in which you can use this and it will definitely work:
     * <ul>
     *     <li>in the handler for receiving a packet, since the context object is guaranteed to match {@link C}</li>
     *     <li>in versioned code, since you can name {@link C}, but really you should just use {@link #send} in that case</li>
     * </ul>
     *
     * @param connection it's gotta be {@link C} but also we do an unchecked cast
     * @param packet packet to send
     */
    @SuppressWarnings("unchecked")
    default void sendUnchecked(@NotNull Object connection, @NotNull Packet<?> packet) {
        send((C) connection, packet);
    }

    /**
     * {@link #send} with type checking.
     *
     * @return {@code true} if OK
     */
    boolean trySend(Object maybeConnection, @NotNull Packet<?> packet);
}
