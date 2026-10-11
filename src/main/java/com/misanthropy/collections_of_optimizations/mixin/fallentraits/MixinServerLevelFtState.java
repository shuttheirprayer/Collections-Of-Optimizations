package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.core.FtLevelState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevelFtState implements FtLevelState {

    @Unique
    private int coo$ftSpawnTick;

    @Unique
    private double[] coo$ftSpawnBonuses;

    @Unique
    private Set<LivingEntity> coo$ftDevourers;

    @Override
    public int coo$ftSpawnTick() {
        return this.coo$ftSpawnTick;
    }

    @Override
    public double[] coo$ftSpawnBonuses() {
        return this.coo$ftSpawnBonuses;
    }

    @Override
    public void coo$ftSetSpawnBonuses(int tick, double[] bonuses) {
        this.coo$ftSpawnTick = tick;
        this.coo$ftSpawnBonuses = bonuses;
    }

    @Override
    public Set<LivingEntity> coo$ftDevourers() {
        Set<LivingEntity> devourers = this.coo$ftDevourers;
        if (devourers == null) {
            devourers = Collections.newSetFromMap(new WeakHashMap<>());
            this.coo$ftDevourers = devourers;
        }
        return devourers;
    }
}
