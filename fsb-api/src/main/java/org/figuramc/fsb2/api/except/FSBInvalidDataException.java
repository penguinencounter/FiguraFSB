package org.figuramc.fsb2.api.except;

/**
 * Data is bad in some way.
 */
public class FSBInvalidDataException extends FSBException {
    public FSBInvalidDataException() {
    }

    public FSBInvalidDataException(String message) {
        super(message);
    }

    public FSBInvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public FSBInvalidDataException(Throwable cause) {
        super(cause);
    }
}
