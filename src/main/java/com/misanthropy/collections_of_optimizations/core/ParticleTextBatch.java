package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.renderer.MultiBufferSource;

public final class ParticleTextBatch {

    private static boolean rendering;
    private static MultiBufferSource.BufferSource pending;

    private ParticleTextBatch() {
    }

    public static void begin() {
        rendering = true;
    }

    public static boolean defer(MultiBufferSource.BufferSource buffer) {
        if (!rendering) {
            return false;
        }
        if (pending != null && pending != buffer) {
            pending.endBatch();
        }
        pending = buffer;
        return true;
    }

    public static void flush() {
        MultiBufferSource.BufferSource buffer = pending;
        if (buffer != null) {
            pending = null;
            buffer.endBatch();
        }
    }

    public static void end() {
        rendering = false;
        flush();
    }
}
