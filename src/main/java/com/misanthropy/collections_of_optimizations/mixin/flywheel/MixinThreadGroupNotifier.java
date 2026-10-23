package com.misanthropy.collections_of_optimizations.mixin.flywheel;

import com.misanthropy.collections_of_optimizations.core.ParkingNotifier;
import dev.engine_room.flywheel.impl.task.ThreadGroupNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ThreadGroupNotifier.class, remap = false)
public abstract class MixinThreadGroupNotifier implements ParkingNotifier.Holder {

    @Unique
    private final ParkingNotifier coo$parking = new ParkingNotifier();

    @Override
    public ParkingNotifier coo$parking() {
        return this.coo$parking;
    }
}
