package com.misanthropy.collections_of_optimizations.mixin.immersiveoptimization;

import com.misanthropy.collections_of_optimizations.core.ImmersiveBlockEntitySchedule;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class MixinLevelImmersiveSchedule implements ImmersiveBlockEntitySchedule.Holder {

    @Unique
    private ImmersiveBlockEntitySchedule coo$immersiveSchedule;

    @Override
    public ImmersiveBlockEntitySchedule coo$immersiveSchedule() {
        ImmersiveBlockEntitySchedule schedule = this.coo$immersiveSchedule;
        if (schedule == null) {
            schedule = new ImmersiveBlockEntitySchedule();
            this.coo$immersiveSchedule = schedule;
        }
        return schedule;
    }
}
