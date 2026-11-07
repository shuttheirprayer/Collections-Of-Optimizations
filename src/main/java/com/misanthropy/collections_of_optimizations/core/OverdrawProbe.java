package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import java.util.HashMap;
import java.util.Map;

public final class OverdrawProbe {

    public static final boolean ENABLED = Boolean.getBoolean("coo.overdrawProbe");

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final int RING = 4;
    private static final Map<RenderType, Slot> SLOTS = new HashMap<>();
    private static Slot active;
    private static long frames;

    private OverdrawProbe() {
    }

    private static final class Slot {
        final String name;
        final int[] queries = new int[RING];
        final int[] primitives = new int[RING];
        final boolean[] pending = new boolean[RING];
        int index;
        long samples;
        long triangles;
        long reads;
        double screen;

        Slot(String name) {
            this.name = name;
            GL15.glGenQueries(this.queries);
            GL15.glGenQueries(this.primitives);
        }
    }

    public static void begin(RenderType type) {
        if (active != null || IrisState.shadowPass()) {
            return;
        }
        Slot slot = SLOTS.computeIfAbsent(type, t -> new Slot(layerName(t)));
        int query = slot.queries[slot.index];
        if (slot.pending[slot.index]) {
            if (GL15.glGetQueryObjecti(query, GL15.GL_QUERY_RESULT_AVAILABLE) == 0) {
                return;
            }
            slot.samples += GL33.glGetQueryObjecti64(query, GL15.GL_QUERY_RESULT);
            slot.triangles += GL33.glGetQueryObjecti64(slot.primitives[slot.index], GL15.GL_QUERY_RESULT);
            slot.reads++;
            slot.pending[slot.index] = false;
        }
        GL15.glBeginQuery(GL15.GL_SAMPLES_PASSED, query);
        GL15.glBeginQuery(GL30.GL_PRIMITIVES_GENERATED, slot.primitives[slot.index]);
        active = slot;
    }

    private static String layerName(RenderType type) {
        String text = type.toString();
        int open = text.indexOf('[');
        int colon = text.indexOf(':', open + 1);
        return open >= 0 && colon > open ? text.substring(open + 1, colon) : text;
    }

    public static void end() {
        Slot slot = active;
        if (slot == null) {
            return;
        }
        active = null;
        GL15.glEndQuery(GL30.GL_PRIMITIVES_GENERATED);
        GL15.glEndQuery(GL15.GL_SAMPLES_PASSED);
        slot.pending[slot.index] = true;
        slot.index = (slot.index + 1) % RING;
        var window = Minecraft.getInstance().getWindow();
        slot.screen = (double) window.getWidth() * window.getHeight();
    }

    public static void frame() {
        if (++frames % 600 != 0) {
            return;
        }
        StringBuilder line = new StringBuilder("[overdraw]");
        for (Slot slot : SLOTS.values()) {
            if (slot.reads > 0) {
                line.append(' ').append(slot.name).append('=').append(String.format("%.3f", slot.samples / (double) slot.reads / slot.screen))
                        .append('/').append(slot.triangles / slot.reads).append("tri");
                slot.samples = 0;
                slot.triangles = 0;
                slot.reads = 0;
            }
        }
        LOGGER.info(line);
    }
}
