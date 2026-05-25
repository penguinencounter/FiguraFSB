package org.figuramc.fsb.api.events.users;

import org.figuramc.fsb.api.events.Event;

import java.util.UUID;

public class UserLoadingExceptionEvent extends Event {
    private final UUID user;
    private final Exception exception;

    public UserLoadingExceptionEvent(UUID user, Exception exception) {
        this.user = user;
        this.exception = exception;
    }

    public UUID user() {
        return user;
    }

    public Exception exception() {
        return exception;
    }
}
