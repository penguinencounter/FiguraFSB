package org.figuramc.fsb.internals;

import java.lang.invoke.MethodHandle;

public final class PolymorphicBindings {
    /**
     * make the {@link MethodHandle} accept {@link Object} as its {@code this} parameter,
     * so that it can be erased and is not required on compileClasspath
     */
    public static MethodHandle eraseThis(MethodHandle target) {
        return target.asType(target.type().changeParameterType(0, Object.class));
    }
}
