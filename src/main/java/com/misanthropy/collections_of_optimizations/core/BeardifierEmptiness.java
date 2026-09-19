package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.mixin.vanilla.BeardifierAccessor;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunctions;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class BeardifierEmptiness {

    private static final String[] ENHANCED_GETTERS = {
            "getEnhancedRigidIterator",
            "getEnhancedJunctionIterator",
            "moogs_structures_getEnhancedPieceIterator",
            "moogs_structures_getEnhancedJunctionIterator"
    };

    private static MethodHandle[] enhanced;

    private static final DensityFunctions.BeardifierOrMarker EMPTY_MARKER = resolveMarker();

    public static DensityFunctions.BeardifierOrMarker emptyMarker() {
        return EMPTY_MARKER;
    }

    private static DensityFunctions.BeardifierOrMarker resolveMarker() {
        for (Class<?> nested : DensityFunctions.class.getDeclaredClasses()) {
            if (nested.getSimpleName().equals("BeardifierMarker")) {
                Object[] constants = nested.getEnumConstants();
                if (constants != null && constants.length > 0 && constants[0] instanceof DensityFunctions.BeardifierOrMarker marker) {
                    return marker;
                }
            }
        }
        return null;
    }

    private BeardifierEmptiness() {
    }

    public static boolean isEmpty(Beardifier beardifier) {
        BeardifierAccessor accessor = (BeardifierAccessor) beardifier;
        if (accessor.coo$pieceIterator().hasNext() || accessor.coo$junctionIterator().hasNext()) {
            return false;
        }
        for (MethodHandle getter : enhancedGetters()) {
            try {
                Object iterator = getter.invoke(beardifier);
                if (iterator instanceof Iterator<?> it && it.hasNext()) {
                    return false;
                }
            } catch (Throwable t) {
                return false;
            }
        }
        return true;
    }

    private static MethodHandle[] enhancedGetters() {
        MethodHandle[] resolved = enhanced;
        if (resolved == null) {
            List<MethodHandle> found = new ArrayList<>();
            for (String name : ENHANCED_GETTERS) {
                try {
                    found.add(MethodHandles.lookup().unreflect(Beardifier.class.getMethod(name))
                            .asType(MethodType.methodType(Object.class, Beardifier.class)));
                } catch (ReflectiveOperationException | IllegalArgumentException absent) {
                }
            }
            resolved = found.toArray(new MethodHandle[0]);
            enhanced = resolved;
        }
        return resolved;
    }
}
