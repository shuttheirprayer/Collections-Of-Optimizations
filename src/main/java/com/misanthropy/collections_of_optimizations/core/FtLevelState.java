package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public interface FtLevelState {

    int coo$ftSpawnTick();

    double[] coo$ftSpawnBonuses();

    void coo$ftSetSpawnBonuses(int tick, double[] bonuses);

    Set<LivingEntity> coo$ftDevourers();
}
