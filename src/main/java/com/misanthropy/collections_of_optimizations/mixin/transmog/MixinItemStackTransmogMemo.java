package com.misanthropy.collections_of_optimizations.mixin.transmog;

import com.misanthropy.collections_of_optimizations.core.FrameStamp;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class MixinItemStackTransmogMemo implements FrameStamp.TransmogMemo {

    @Unique
    private int coo$transmogFrame;

    @Unique
    private boolean coo$transmogged;

    @Override
    public int coo$transmogFrame() {
        return this.coo$transmogFrame;
    }

    @Override
    public boolean coo$transmogged() {
        return this.coo$transmogged;
    }

    @Override
    public void coo$transmogMemo(int frame, boolean transmogged) {
        this.coo$transmogFrame = frame;
        this.coo$transmogged = transmogged;
    }
}
