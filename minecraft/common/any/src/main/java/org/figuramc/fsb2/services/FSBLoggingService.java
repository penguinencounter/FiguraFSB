package org.figuramc.fsb2.services;

public interface FSBLoggingService {
    /**
     * <p>
     * The returned object is expected to implement these forms for each of {@code trace},
     * {@code debug}, {@code info}, {@code warn}, and {@code error}:
     * </p>
     * <ol>
     *     <li>{@code log(String message)}</li>
     *     <li>{@code log(String format, Object... arguments)}</li>
     *     <li>{@code log(String format, Throwable throwable)}</li>
     * </ol>
     *
     * @return any object conforming to the interface, not remapped.
     */
    Object getLogger();
}
