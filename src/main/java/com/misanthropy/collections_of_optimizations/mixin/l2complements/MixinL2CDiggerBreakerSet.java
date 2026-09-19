package com.misanthropy.collections_of_optimizations.mixin.l2complements;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(targets = "dev.xkmc.l2complements.content.enchantment.digging.RangeDiggingEnchantment", remap = false)
public abstract class MixinL2CDiggerBreakerSet {

    @Mutable
    @Shadow
    @Final
    private static Set<UUID> BREAKER;

    @Inject(method = "<clinit>", at = @At("TAIL"), require = 0)
    private static void coo$useConcurrentBreakerSet(CallbackInfo ci) {
        if (!CoOConfig.l2complementsFixBreakerSetRace) {
            return;
        }
        Set<UUID> current = BREAKER;
        Set<UUID> replacement = ConcurrentHashMap.newKeySet();
        if (current != null && !current.isEmpty()) {
            replacement.addAll(current);
        }
        BREAKER = replacement;
    }
}
