package com.misanthropy.collections_of_optimizations.mixin.betterspellcasting;

import com.misanthropy.collections_of_optimizations.core.BscSwingStampHolder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class MixinBscPlayerSwingStamp implements BscSwingStampHolder {

    @Unique
    private long coo$bscSwingTick;

    @Override
    public long coo$bscSwingTick() {
        return this.coo$bscSwingTick;
    }

    @Override
    public void coo$setBscSwingTick(long tick) {
        this.coo$bscSwingTick = tick;
    }
}
