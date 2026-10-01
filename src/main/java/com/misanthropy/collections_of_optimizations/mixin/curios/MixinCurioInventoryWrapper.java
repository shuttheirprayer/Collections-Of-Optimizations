package com.misanthropy.collections_of_optimizations.mixin.curios;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Optional;

@Mixin(value = CurioInventoryCapability.CurioInventoryWrapper.class, remap = false)
public abstract class MixinCurioInventoryWrapper implements ICuriosItemHandler {

    @Inject(
            method = "findFirstCurio(Lnet/minecraft/world/item/Item;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$fastFindFirstMiss(Item item, CallbackInfoReturnable<Optional<SlotResult>> cir) {
        if (!CoOConfig.curiosFastFindFirstMiss) {
            return;
        }
        LivingEntity wearer = this.getWearer();
        if (wearer != null && !CurioPresenceCache.mayHaveEquipped(wearer, item)) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(method = "setCurios(Ljava/util/Map;)V", at = @At("RETURN"), require = 0)
    private void coo$invalidateOnSetCurios(Map<String, ICurioStacksHandler> curios, CallbackInfo ci) {
        CurioPresenceCache.invalidate(this.getWearer());
    }

    @Inject(method = "reset()V", at = @At("RETURN"), require = 0)
    private void coo$invalidateOnReset(CallbackInfo ci) {
        CurioPresenceCache.invalidate(this.getWearer());
    }
}
