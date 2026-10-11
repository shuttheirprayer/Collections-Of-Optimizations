package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.biome.Climate;

import java.lang.ref.WeakReference;

public final class ClimateSampleMemo {

    private static final ThreadLocal<ClimateSampleMemo> LOCAL = ThreadLocal.withInitial(ClimateSampleMemo::new);

    private WeakReference<Object> sampler;
    private int x;
    private int y;
    private int z;
    private Climate.TargetPoint result;

    private ClimateSampleMemo() {
    }

    public static ClimateSampleMemo get() {
        return LOCAL.get();
    }

    public Climate.TargetPoint lookup(Object sampler, int x, int y, int z) {
        Climate.TargetPoint result = this.result;
        if (result != null && this.x == x && this.y == y && this.z == z && this.sampler.get() == sampler) {
            return result;
        }
        return null;
    }

    public void store(Object sampler, int x, int y, int z, Climate.TargetPoint result) {
        WeakReference<Object> held = this.sampler;
        if (held == null || held.get() != sampler) {
            this.sampler = new WeakReference<>(sampler);
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.result = result;
    }

}
