package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.entity.ai.goal.Goal;

public final class ThrottledCanUseGoal extends Goal {

    private final Goal delegate;
    private final int interval;
    private int cooldown;

    public ThrottledCanUseGoal(Goal delegate, int interval) {
        this.delegate = delegate;
        this.interval = Math.max(1, interval);
        setFlags(delegate.getFlags());
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        if (delegate.canUse()) {
            return true;
        }
        cooldown = interval;
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return delegate.canContinueToUse();
    }

    @Override
    public boolean isInterruptable() {
        return delegate.isInterruptable();
    }

    @Override
    public void start() {
        cooldown = 0;
        delegate.start();
    }

    @Override
    public void stop() {
        cooldown = 0;
        delegate.stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return delegate.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        delegate.tick();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
