package com.misanthropy.collections_of_optimizations.mixin.refinedmod;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Pseudo
@Mixin(targets = "net.refinedrain.refinedmod.effect.hunters_mark.HuntersMarkHandler", remap = false)
public abstract class MixinHuntersMarkMap {

    @Shadow
    @Final
    private static Map<UUID, UUID> markedEntities;

    @Inject(method = "onLivingHurt", at = @At("HEAD"), require = 0)
    private static void coo$boundMarkedEntities(LivingHurtEvent event, CallbackInfo ci) {
        int cap = CoOConfig.refinedmodHuntersMarkMapCap;
        if (cap <= 0) {
            return;
        }
        Map<UUID, UUID> map = markedEntities;
        if (map != null && map.size() > cap) {
            map.clear();
        }
    }
}
