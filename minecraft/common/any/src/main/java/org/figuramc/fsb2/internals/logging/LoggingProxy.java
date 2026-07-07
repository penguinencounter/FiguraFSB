package org.figuramc.fsb2.internals.logging;

import org.figuramc.fsb2.internals.BindingException;
import org.figuramc.fsb2.api.utils.FSBLogger;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.figuramc.fsb2.internals.PolymorphicBindings.produceLambda;

/**
 * <p>
 * Turn any logging object with the right method shapes into a {@link FSBLogger}.
 * </p>
 * <p>
 * The target object needs to support the following three forms for each log level:
 * </p>
 * <ol>
 *     <li>{@code log(String message)}</li>
 *     <li>{@code log(String format, Object... args)}</li>
 *     <li>{@code log(String message, Throwable throwable)}</li>
 * </ol>
 */
public class LoggingProxy implements FSBLogger {
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
    @Override
    public void trace(String message) {
        TRACE_1.log(message);
    }

    @Override
    public void trace(String format, Object... arguments) {
        TRACE_2.log(format, arguments);
    }

    @Override
    public void trace(String message, Throwable throwable) {
        TRACE_3.log(message, throwable);
    }

    @Override
    public void debug(String message) {
        DEBUG_1.log(message);
    }

    @Override
    public void debug(String format, Object... arguments) {
        DEBUG_2.log(format, arguments);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        DEBUG_3.log(message, throwable);
    }

    @Override
    public void info(String message) {
        INFO_1.log(message);
    }

    @Override
    public void info(String format, Object... arguments) {
        INFO_2.log(format, arguments);
    }

    @Override
    public void info(String message, Throwable throwable) {
        INFO_3.log(message, throwable);
    }

    @Override
    public void warn(String message) {
        WARN_1.log(message);
    }

    @Override
    public void warn(String format, Object... arguments) {
        WARN_2.log(format, arguments);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        WARN_3.log(message, throwable);
    }

    @Override
    public void error(String message) {
        ERROR_1.log(message);
    }

    @Override
    public void error(String format, Object... arguments) {
        ERROR_2.log(format, arguments);
    }

    @Override
    public void error(String message, Throwable throwable) {
        ERROR_3.log(message, throwable);
    }
}
