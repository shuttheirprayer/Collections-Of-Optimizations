package com.misanthropy.collections_of_optimizations.mixin.l2archery;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2ArcheryClientCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.xkmc.l2archery.events.GenericEventHandler", remap = false)
public abstract class MixinL2ArcheryBowFov {

    @Inject(
            method = "fov(Lnet/minecraftforge/client/event/ComputeFovModifierEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipIdleBowFov(ComputeFovModifierEvent event, CallbackInfo ci) {
        if (!CoOConfig.l2archerySkipIdleBowFov) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options == null) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null || player.getTicksUsingItem() != 0) {
            return;
        }
        if (!L2ArcheryClientCache.isGenericBow(player.getMainHandItem().getItem())) {
            return;
        }

        double scale = minecraft.options.fovEffectScale().get();
        event.setNewFovModifier((float) Mth.lerp(scale, 1.0D, event.getFovModifier()));
        ci.cancel();
    }
}
