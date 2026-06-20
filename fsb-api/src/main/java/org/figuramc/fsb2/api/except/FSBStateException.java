package org.figuramc.fsb2.api.except;

/**
 * Checked equivalent of {@link IllegalStateException}.
 */
public class FSBStateException extends FSBException {
    public FSBStateException() {
        super();
    }

    public FSBStateException(String message) {
        super(message);
    }

    public FSBStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FSBStateException(Throwable cause) {
        super(cause);
    }
}
