package org.figuramc.fsb2.internals;

/**
 * An error in method resolution when binding to unknown external libraries
 */
public class BindingException extends RuntimeException {
    public BindingException(String message) {
        super(message);
    }

    public BindingException(Throwable cause) {
        super(cause);
    }
}
