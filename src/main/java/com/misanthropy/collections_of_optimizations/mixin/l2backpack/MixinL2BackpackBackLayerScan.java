package com.misanthropy.collections_of_optimizations.mixin.l2backpack;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2BackpackRenderSlotCache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(targets = "dev.xkmc.l2backpack.compat.CuriosCompat", remap = false)
public abstract class MixinL2BackpackBackLayerScan {

    @Inject(
            method = "getRenderingSlotImpl(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$cachedRenderingSlot(LivingEntity entity, Predicate<ItemStack> filter, CallbackInfoReturnable<Optional<ItemStack>> cir) {
        if (!CoOConfig.l2backpackCacheBackLayerScan) {
            return;
        }

        Optional<ItemStack> cached = L2BackpackRenderSlotCache.get(filter, entity);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "getRenderingSlotImpl(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Optional;",
            at = @At("RETURN"),
            require = 0
    )
    private static void coo$storeRenderingSlot(LivingEntity entity, Predicate<ItemStack> filter, CallbackInfoReturnable<Optional<ItemStack>> cir) {
        if (!CoOConfig.l2backpackCacheBackLayerScan) {
            return;
        }

        L2BackpackRenderSlotCache.put(filter, entity, cir.getReturnValue());
    }
}
