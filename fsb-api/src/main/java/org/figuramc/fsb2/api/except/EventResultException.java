package org.figuramc.fsb2.api.except;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EventResultException extends IllegalStateException {
    public static final String YAPPING = "This is probably caused by one of your FSB addons being broken!";

    public static @NotNull EventResultException noResult(
            @NotNull String eventName,
            @NotNull Class<?> type,
            @Nullable String additional,
            @Nullable Throwable cause
    ) {
        StringBuilder builder = new StringBuilder();
        String typeName = type.getSimpleName();
        if (typeName.isEmpty()) typeName = type.getName();
        builder.append(String.format(
                "Mishandled event\nThe %s event resulted in no result, but a non-null %s was expected.",
                eventName, typeName
        ));
        builder.append('\n').append(YAPPING);
        if (additional != null) {
            builder.append('\n').append(additional);
        }
        if (cause != null) return new EventResultException(builder.toString(), cause);
        return new EventResultException(builder.toString());
    }

    private EventResultException(String message) {
        super(message);
    }

    private EventResultException(String message, Throwable cause) {
        super(message, cause);
    }
}
