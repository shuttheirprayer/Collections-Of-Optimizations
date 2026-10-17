package com.misanthropy.collections_of_optimizations.mixin.immersiveaircraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import immersive_aircraft.client.OverlayRenderer;
import immersive_aircraft.entity.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = OverlayRenderer.class, remap = false)
public abstract class MixinOverlayRenderer {

    @WrapMethod(method = "renderOverlay")
    private static int coo$batchOverlayDraws(GuiGraphics context, float tickDelta, int barHeightOffset,
                                             Operation<Integer> original) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!CoOConfig.immersiveaircraftBatchOverlay || minecraft.player == null
                || !(minecraft.player.getVehicle() instanceof VehicleEntity)) {
            return original.call(context, tickDelta, barHeightOffset);
        }

        int[] result = new int[1];
        context.drawManaged(() -> result[0] = original.call(context, tickDelta, barHeightOffset));
        return result[0];
    }
}
