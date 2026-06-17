package org.figuramc.fsb2.api.except;

/**
 * FSB's checked exception class.
 */
public abstract class FSBException extends Exception {
    public FSBException() {
        super();
    }

    public FSBException(String message) {
        super(message);
    }

    public FSBException(String message, Throwable cause) {
        super(message, cause);
    }

    public FSBException(Throwable cause) {
        super(cause);
    }
}
