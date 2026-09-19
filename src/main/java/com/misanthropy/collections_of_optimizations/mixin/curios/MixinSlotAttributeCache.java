package com.misanthropy.collections_of_optimizations.mixin.curios;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.SlotAttribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = SlotAttribute.class, remap = false)
public abstract class MixinSlotAttributeCache {

    @Mutable
    @Shadow
    @Final
    private static Map<String, SlotAttribute> SLOT_ATTRIBUTES;

    @Inject(method = "<clinit>", at = @At("TAIL"), require = 0)
    private static void coo$useConcurrentSlotAttributeCache(CallbackInfo ci) {
        if (!CoOConfig.curiosThreadSafeCaches) {
            return;
        }
        Map<String, SlotAttribute> current = SLOT_ATTRIBUTES;
        SLOT_ATTRIBUTES = current == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(current);
    }
}
