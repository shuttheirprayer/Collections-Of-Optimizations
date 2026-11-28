package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.IdleEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntityIdleState implements IdleEntities.State {

    @Unique
    private Vec3 coo$idlePos;

    @Unique
    private Vec3 coo$idleMotion;

    @Unique
    private BlockState coo$idleFeet;

    @Unique
    private BlockState coo$idleBelow;

    @Override
    public Vec3 coo$idlePos() {
        return this.coo$idlePos;
    }

    @Override
    public Vec3 coo$idleMotion() {
        return this.coo$idleMotion;
    }

    @Override
    public BlockState coo$idleFeet() {
        return this.coo$idleFeet;
    }

    @Override
    public BlockState coo$idleBelow() {
        return this.coo$idleBelow;
    }

    @Override
    public void coo$sleep(Vec3 pos, Vec3 motion, BlockState feet, BlockState below) {
        this.coo$idlePos = pos;
        this.coo$idleMotion = motion;
        this.coo$idleFeet = feet;
        this.coo$idleBelow = below;
    }

    @Override
    public boolean coo$skipTick() {
        return false;
    }

    @Override
    public boolean coo$mergeable() {
        return false;
    }

    @Override
    public void coo$wake() {
        this.coo$idlePos = null;
        this.coo$idleMotion = null;
        this.coo$idleFeet = null;
        this.coo$idleBelow = null;
    }
}
