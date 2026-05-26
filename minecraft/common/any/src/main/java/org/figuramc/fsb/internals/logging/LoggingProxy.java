package org.figuramc.fsb.internals.logging;

import org.figuramc.fsb.internals.BindingException;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.figuramc.fsb.internals.PolymorphicBindings.eraseThis;

/**
 * <p>
 * Proxy for a few types of logging framework.
 * <a href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/lang/invoke/MethodHandles.html">java.lang.invoke documentation</a>
 * </p>
 * <p>
 * We aim to support the following three forms for each log level:
 * </p>
 * <ol>
 *     <li>{@code log(String message)}</li>
 *     <li>{@code log(String format, Object... args)}</li>
 *     <li>{@code log(String message, Throwable throwable)}</li>
 * </ol>
 */
public class LoggingProxy {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.publicLookup();
    private static final MethodType FORM_1 = MethodType.methodType(void.class, String.class);
    private static final MethodType FORM_2 = MethodType.methodType(void.class, String.class, Object[].class);
    private static final MethodType FORM_3 = MethodType.methodType(void.class, String.class, Throwable.class);

    @NotNull
    private final MethodHandle TRACE_1;
    @NotNull
    private final MethodHandle TRACE_2;
    @NotNull
    private final MethodHandle TRACE_3;
    @NotNull
    private final MethodHandle DEBUG_1;
    @NotNull
    private final MethodHandle DEBUG_2;
    @NotNull
    private final MethodHandle DEBUG_3;
    @NotNull
    private final MethodHandle INFO_1;
    @NotNull
    private final MethodHandle INFO_2;
    @NotNull
    private final MethodHandle INFO_3;
    @NotNull
    private final MethodHandle WARN_1;
    @NotNull
    private final MethodHandle WARN_2;
    @NotNull
    private final MethodHandle WARN_3;
    @NotNull
    private final MethodHandle ERROR_1;
    @NotNull
    private final MethodHandle ERROR_2;
    @NotNull
    private final MethodHandle ERROR_3;
    @NotNull
    private final Object wrapper;

    public LoggingProxy(@NotNull Object wrapper) {
        this.wrapper = wrapper;
        try {
            Class<?> theClass = wrapper.getClass();
            TRACE_1 = eraseThis(LOOKUP.findVirtual(theClass, "trace", FORM_1));
            TRACE_2 = eraseThis(LOOKUP.findVirtual(theClass, "trace", FORM_2));
            TRACE_3 = eraseThis(LOOKUP.findVirtual(theClass, "trace", FORM_3));
            DEBUG_1 = eraseThis(LOOKUP.findVirtual(theClass, "debug", FORM_1));
            DEBUG_2 = eraseThis(LOOKUP.findVirtual(theClass, "debug", FORM_2));
            DEBUG_3 = eraseThis(LOOKUP.findVirtual(theClass, "debug", FORM_3));
            INFO_1 = eraseThis(LOOKUP.findVirtual(theClass, "info", FORM_1));
            INFO_2 = eraseThis(LOOKUP.findVirtual(theClass, "info", FORM_2));
            INFO_3 = eraseThis(LOOKUP.findVirtual(theClass, "info", FORM_3));
            WARN_1 = eraseThis(LOOKUP.findVirtual(theClass, "warn", FORM_1));
            WARN_2 = eraseThis(LOOKUP.findVirtual(theClass, "warn", FORM_2));
            WARN_3 = eraseThis(LOOKUP.findVirtual(theClass, "warn", FORM_3));
            ERROR_1 = eraseThis(LOOKUP.findVirtual(theClass, "error", FORM_1));
            ERROR_2 = eraseThis(LOOKUP.findVirtual(theClass, "error", FORM_2));
            ERROR_3 = eraseThis(LOOKUP.findVirtual(theClass, "error", FORM_3));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new BindingException(e);
        }
    }

    // use a template engine to save yourself the headache tbh
    public void trace(String message) {
        try {
            TRACE_1.invokeExact(wrapper, message);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void trace(String format, Object... arguments) {
        try {
            TRACE_2.invokeExact(wrapper, format, arguments);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void trace(String message, Throwable throwable) {
        try {
            TRACE_3.invokeExact(wrapper, message, throwable);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void debug(String message) {
        try {
            DEBUG_1.invokeExact(wrapper, message);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void debug(String format, Object... arguments) {
        try {
            DEBUG_2.invokeExact(wrapper, format, arguments);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void debug(String message, Throwable throwable) {
        try {
            DEBUG_3.invokeExact(wrapper, message, throwable);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void info(String message) {
        try {
            INFO_1.invokeExact(wrapper, message);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void info(String format, Object... arguments) {
        try {
            INFO_2.invokeExact(wrapper, format, arguments);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void info(String message, Throwable throwable) {
        try {
            INFO_3.invokeExact(wrapper, message, throwable);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void warn(String message) {
        try {
            WARN_1.invokeExact(wrapper, message);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void warn(String format, Object... arguments) {
        try {
            WARN_2.invokeExact(wrapper, format, arguments);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void warn(String message, Throwable throwable) {
        try {
            WARN_3.invokeExact(wrapper, message, throwable);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void error(String message) {
        try {
            ERROR_1.invokeExact(wrapper, message);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void error(String format, Object... arguments) {
        try {
            ERROR_2.invokeExact(wrapper, format, arguments);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void error(String message, Throwable throwable) {
        try {
            ERROR_3.invokeExact(wrapper, message, throwable);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
