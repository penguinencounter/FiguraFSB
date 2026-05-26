package org.figuramc.fsb.internals.logging;

import org.figuramc.fsb.internals.BindingException;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.figuramc.fsb.internals.PolymorphicBindings.produceLambda;

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
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    @FunctionalInterface
    public interface Form1 {
        void log(String message);
    }

    @FunctionalInterface
    public interface Form2 {
        void log(String formatting, Object... arguments);
    }

    @FunctionalInterface
    public interface Form3 {
        void log(String message, Throwable throwable);
    }

    private static final MethodType FORM_1 = MethodType.methodType(void.class, String.class);
    private static final MethodType FORM_2 = MethodType.methodType(void.class, String.class, Object[].class);
    private static final MethodType FORM_3 = MethodType.methodType(void.class, String.class, Throwable.class);

    @NotNull
    private final Form1 TRACE_1;
    @NotNull
    private final Form2 TRACE_2;
    @NotNull
    private final Form3 TRACE_3;
    @NotNull
    private final Form1 DEBUG_1;
    @NotNull
    private final Form2 DEBUG_2;
    @NotNull
    private final Form3 DEBUG_3;
    @NotNull
    private final Form1 INFO_1;
    @NotNull
    private final Form2 INFO_2;
    @NotNull
    private final Form3 INFO_3;
    @NotNull
    private final Form1 WARN_1;
    @NotNull
    private final Form2 WARN_2;
    @NotNull
    private final Form3 WARN_3;
    @NotNull
    private final Form1 ERROR_1;
    @NotNull
    private final Form2 ERROR_2;
    @NotNull
    private final Form3 ERROR_3;
    @NotNull
    private final Object wrapper;

    private Form1 generateForm1(Class<?> wrapperClass,
                                String name) throws NoSuchMethodException, IllegalAccessException, LambdaConversionException {
        return produceLambda(
                LOOKUP,
                Form1.class,
                "log",
                wrapper,
                FORM_1,
                LOOKUP.findVirtual(wrapperClass, name, FORM_1)
        );
    }

    private Form2 generateForm2(Class<?> wrapperClass,
                                String name) throws NoSuchMethodException, IllegalAccessException, LambdaConversionException {
        return produceLambda(
                LOOKUP,
                Form2.class,
                "log",
                wrapper,
                FORM_2,
                LOOKUP.findVirtual(wrapperClass, name, FORM_2)
        );
    }

    private Form3 generateForm3(Class<?> wrapperClass,
                                String name) throws NoSuchMethodException, IllegalAccessException, LambdaConversionException {
        return produceLambda(
                LOOKUP,
                Form3.class,
                "log",
                wrapper,
                FORM_3,
                LOOKUP.findVirtual(wrapperClass, name, FORM_3)
        );
    }

    public LoggingProxy(@NotNull Object wrapper) {
        this.wrapper = wrapper;
        try {
            Class<?> theClass = wrapper.getClass();
            TRACE_1 = generateForm1(theClass, "trace");
            TRACE_2 = generateForm2(theClass, "trace");
            TRACE_3 = generateForm3(theClass, "trace");
            DEBUG_1 = generateForm1(theClass, "debug");
            DEBUG_2 = generateForm2(theClass, "debug");
            DEBUG_3 = generateForm3(theClass, "debug");
            INFO_1 = generateForm1(theClass, "info");
            INFO_2 = generateForm2(theClass, "info");
            INFO_3 = generateForm3(theClass, "info");
            WARN_1 = generateForm1(theClass, "warn");
            WARN_2 = generateForm2(theClass, "warn");
            WARN_3 = generateForm3(theClass, "warn");
            ERROR_1 = generateForm1(theClass, "error");
            ERROR_2 = generateForm2(theClass, "error");
            ERROR_3 = generateForm3(theClass, "error");
        } catch (NoSuchMethodException | IllegalAccessException | LambdaConversionException e) {
            throw new BindingException(e);
        }
    }

    // use a template engine to save yourself the headache tbh
    public void trace(String message) {
        TRACE_1.log(message);
    }

    public void trace(String format, Object... arguments) {
        TRACE_2.log(format, arguments);
    }

    public void trace(String message, Throwable throwable) {
        TRACE_3.log(message, throwable);
    }

    public void debug(String message) {
        DEBUG_1.log(message);
    }

    public void debug(String format, Object... arguments) {
        DEBUG_2.log(format, arguments);
    }

    public void debug(String message, Throwable throwable) {
        DEBUG_3.log(message, throwable);
    }

    public void info(String message) {
        INFO_1.log(message);
    }

    public void info(String format, Object... arguments) {
        INFO_2.log(format, arguments);
    }

    public void info(String message, Throwable throwable) {
        INFO_3.log(message, throwable);
    }

    public void warn(String message) {
        WARN_1.log(message);
    }

    public void warn(String format, Object... arguments) {
        WARN_2.log(format, arguments);
    }

    public void warn(String message, Throwable throwable) {
        WARN_3.log(message, throwable);
    }

    public void error(String message) {
        ERROR_1.log(message);
    }

    public void error(String format, Object... arguments) {
        ERROR_2.log(format, arguments);
    }

    public void error(String message, Throwable throwable) {
        ERROR_3.log(message, throwable);
    }
}
