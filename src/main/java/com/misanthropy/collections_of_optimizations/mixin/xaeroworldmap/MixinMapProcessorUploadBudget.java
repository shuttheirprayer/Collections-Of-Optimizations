package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;

@Mixin(value = MapProcessor.class, remap = false)
public abstract class MixinMapProcessorUploadBudget {

    @ModifyConstant(
            method = "onRenderProcess",
            constant = @Constant(longValue = 3000000L),
            require = 0
    )
    private long coo$shrinkUploadHeadroom(long stock) {
        if (Minecraft.getInstance().screen instanceof GuiMap) {
            return stock;
        }
        return CoOConfig.xaeroworldmapUploadHeadroomMicros * 1000L;
    }
}
