package org.figuramc.fsb2.api.except;

/**
 * Checked equivalent of {@link IllegalArgumentException}.
 */
public class FSBArgumentException extends FSBException {
    public FSBArgumentException() {
    }

    public FSBArgumentException(String message) {
        super(message);
    }

    public FSBArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public FSBArgumentException(Throwable cause) {
        super(cause);
    }
}
