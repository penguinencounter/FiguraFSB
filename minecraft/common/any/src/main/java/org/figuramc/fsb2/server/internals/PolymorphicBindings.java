package org.figuramc.fsb2.server.internals;

import java.lang.invoke.*;

public final class PolymorphicBindings {
    /**
     * generate a lambda (FunctionalInterface) from a method we can't name.
     * @param lookup notice: must be privileged lookup
     * @param funcInterface functional interface target type
     * @param funcInterfaceSamName name of single abstract method in functional interface
     * @param receiver receiver object
     * @param samType {@link MethodType} of the single abstract method
     * @param rawHandle handle for method to redirect to
     * @return brand new {@link T}
     * @param <T> type of {@code funcInterface}
     * @throws LambdaConversionException if something breaks
     */
    public static <T> T produceLambda(
            MethodHandles.Lookup lookup,
            Class<T> funcInterface,
            String funcInterfaceSamName,
            Object receiver,
            MethodType samType,
            MethodHandle rawHandle
    ) throws LambdaConversionException {
        MethodHandle factory = LambdaMetafactory.metafactory(
                lookup, funcInterfaceSamName,
                MethodType.methodType(funcInterface, receiver.getClass()),
                samType,
                rawHandle,
                samType
        ).getTarget().bindTo(receiver);
        try {
            //noinspection unchecked
            return (T) factory.invoke(); // can't invokeExact because we don't know T
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
