package org.figuramc.fsb2.api.packets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the PacketRecord field in a class that is the 'canonical' one for the packet, in case there are multiple.
 * (The test suite will automatically detect a single static field of the correct type.)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketIdentity {
}
