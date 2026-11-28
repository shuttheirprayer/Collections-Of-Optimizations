package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xaero.map.MapWriter;
import xaero.map.gui.GuiMap;

@Mixin(value = MapWriter.class, remap = false)
public abstract class MixinMapWriterRenderDistance {

    @ModifyReturnValue(method = "getWriteDistance", at = @At("RETURN"), require = 0)
    private int coo$writeWithinRenderDistance(int distance) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!CoOConfig.xaeroworldmapWriteWithinRenderDistance || minecraft.screen instanceof GuiMap) {
            return distance;
        }
        return Math.min(distance, minecraft.options.renderDistance().get());
    }
}
