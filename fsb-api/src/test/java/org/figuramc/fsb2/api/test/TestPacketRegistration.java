package org.figuramc.fsb2.api.test;

import io.github.classgraph.*;
import org.figuramc.fsb2.api.FSBConstants;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.PacketIdentity;
import org.figuramc.fsb2.api.packets.PacketRegistrationExempt;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestPacketRegistration {

    private static boolean isLikePacketRecord(FieldInfo field) {
        try {
            if (!field.isStatic()) return false;
            ClassRefTypeSignature sig = (ClassRefTypeSignature) field.getTypeDescriptor();
            if (sig == null) return false;
            return Packets.PacketRecord.class.isAssignableFrom(sig.loadClass());
        } catch (IllegalArgumentException | ClassCastException e) {
            return false;
        }
    }

    @Test
    public void checkMappingConsistency() {
        for (Map.Entry<Identifier, Packets.PacketRecord<?>> entry : Packets.getAllPackets()
                .entrySet()) {
            Assertions.assertEquals(entry.getKey(), entry.getValue().id);
        }
    }

    @Test
    public void checkAllPacketsAreRegistered() {
        HashSet<Packets.PacketRecord<?>> identities = new HashSet<>(Packets.getAllPackets().values());

        String basePackage = FSBConstants.class.getPackageName();
        try (ScanResult result =
                     new ClassGraph()
                             .acceptPackages(basePackage)
                             .enableClassInfo()
                             .enableFieldInfo()
                             .enableAnnotationInfo()
                             .scan()
        ) {
            for (ClassInfo cls : result.getClassesImplementing(Packet.class)) {
                if (cls.hasAnnotation(PacketRegistrationExempt.class)) continue;
                // find single PacketRecord static field
                List<FieldInfo> matchingFields =
                        cls.getDeclaredFieldInfo()
                                .stream()
                                .filter(TestPacketRegistration::isLikePacketRecord)
                                .collect(Collectors.toCollection(ArrayList::new));
                int firstPassSize = matchingFields.size();
                // If we have more than one we need to narrow on the annotation or else explode
                if (matchingFields.size() > 1) {
                    matchingFields.removeIf(f -> !f.hasAnnotation(PacketIdentity.class));
                }
                if (matchingFields.size() > 1) {
                    throw new IllegalStateException(
                            "could not determine what the PacketRecord for %s is (%d fields with @PacketIdentity; please only select one field)"
                                    .formatted(cls.getName(), matchingFields.size())
                    );
                }
                if (matchingFields.isEmpty() && firstPassSize > 0) {
                    throw new IllegalStateException(
                            "could not determine what the PacketRecord for %s is (%d candidates. use @PacketIdentity to select one)"
                                    .formatted(cls.getName(), firstPassSize)
                    );
                }
                if (matchingFields.isEmpty()) {
                    throw new IllegalStateException(
                            "could not determine what the PacketRecord for %s is (no static fields of the right type)"
                                    .formatted(cls.getName())
                    );
                }

                // ... so what's the value?
                FieldInfo theField = matchingFields.get(0);

                try {
                    Packets.PacketRecord<?> theContent =
                            (Packets.PacketRecord<?>) theField.loadClassAndGetField().get(null);
                    if (!identities.contains(theContent))
                        Assertions.fail(String.format(
                                "! %s (implements Packet) is not registered in Packets' static initialization phase",
                                cls.getName()
                        ));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
