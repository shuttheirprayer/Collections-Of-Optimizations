package com.misanthropy.collections_of_optimizations.mixin.darkdoppelganger;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DoppelBossBarNames;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.bandit.darkdoppelganger.event.ClientEvents", remap = false)
public abstract class MixinDDClientEvents {

    @Inject(
            method = "onCustomizeBossBar",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipForeignBossBars(CustomizeGuiOverlayEvent.BossEventProgress event, CallbackInfo ci) {
        if (!CoOConfig.darkdoppelgangerLeanBossBarNameCheck || event == null) {
            return;
        }
        LerpingBossEvent bossEvent = event.getBossEvent();
        if (bossEvent == null) {
            return;
        }
        if (!DoppelBossBarNames.matches(bossEvent.getName())) {
            ci.cancel();
        }
    }
}
