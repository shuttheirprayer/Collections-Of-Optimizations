package com.misanthropy.collections_of_optimizations.core;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureLikeData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class StructurifyConfigMirror {

    private static final AtomicInteger GENERATION = new AtomicInteger(1);
    private static volatile CheckState checkState;
    private static volatile boolean structureChecksLive;

    private StructurifyConfigMirror() {
    }

    public static void invalidate() {
        GENERATION.incrementAndGet();
    }

    public static int generation() {
        return GENERATION.get();
    }

    public static void markStructureChecksLive() {
        if (!structureChecksLive) {
            structureChecksLive = true;
        }
    }

    public static boolean structureChecksLive() {
        return structureChecksLive;
    }

    // live read
    @Nullable
    public static StructureSetData structureSetData(@Nullable String structureSetId) {
        return structureSetId == null ? null : Structurify.getConfig().getStructureSetData().get(structureSetId);
    }

    public static boolean allStructureChecksDisabled() {
        StructurifyConfig config = Structurify.getConfig();
        if (config.preventStructureOverlap || config.getDebugData().isEnabled()) {
            return false;
        }
        Map<String, StructureNamespaceData> namespaces = config.getStructureNamespaceData();
        Map<String, StructureData> structures = config.getStructureData();
        int generation = GENERATION.get();
        CheckState state = checkState;
        if (state == null
                || state.generation != generation
                || state.namespaces != namespaces
                || state.structures != structures) {
            boolean allDisabled = !anyCheckEnabled(namespaces.values()) && !anyCheckEnabled(structures.values());
            state = new CheckState(generation, namespaces, structures, allDisabled);
            checkState = state;
        }
        return state.allDisabled;
    }

    private static boolean anyCheckEnabled(Collection<? extends StructureLikeData> values) {
        for (StructureLikeData data : values) {
            if (data == null) {
                continue;
            }
            BiomeCheckData biomeCheck = data.getBiomeCheckData();
            if (biomeCheck != null && biomeCheck.isEnabled()) {
                return true;
            }
            FlatnessCheckData flatnessCheck = data.getFlatnessCheckData();
            if (flatnessCheck != null && flatnessCheck.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public static final class Cached<T> {

        private final int generation;
        private final T value;

        public Cached(int generation, T value) {
            this.generation = generation;
            this.value = value;
        }

        public boolean isCurrent() {
            return this.generation == GENERATION.get();
        }

        public T value() {
            return this.value;
        }
    }

    private static final class CheckState {

        private final int generation;
        private final Map<String, StructureNamespaceData> namespaces;
        private final Map<String, StructureData> structures;
        private final boolean allDisabled;

        private CheckState(int generation,
                           Map<String, StructureNamespaceData> namespaces,
                           Map<String, StructureData> structures,
                           boolean allDisabled) {
            this.generation = generation;
            this.namespaces = namespaces;
            this.structures = structures;
            this.allDisabled = allDisabled;
        }
    }
}
