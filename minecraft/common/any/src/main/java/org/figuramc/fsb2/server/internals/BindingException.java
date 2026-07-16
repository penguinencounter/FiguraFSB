package org.figuramc.fsb2.server.internals;

/**
 * An error in method resolution when binding to unknown external libraries
 */
public class BindingException extends RuntimeException {
    public BindingException(String message) {
        super(String.format("your FSB build is probably broken: %s", message));
    }

    public BindingException(Throwable cause) {
        super(cause);
    }
}
