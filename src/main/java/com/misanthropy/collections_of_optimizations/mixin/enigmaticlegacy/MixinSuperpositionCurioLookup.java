package com.misanthropy.collections_of_optimizations.mixin.enigmaticlegacy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(targets = "com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler", remap = false)
public abstract class MixinSuperpositionCurioLookup {

    @Inject(
            method = "hasCurio(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$cachedHasCurio(LivingEntity entity, Item item, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.enigmaticlegacyCachedCurioLookup || entity == null || item == null) {
            return;
        }
        Set<Item> equipped = CurioPresenceCache.equippedItems(entity);
        if (equipped == null) {
            return;
        }
        cir.setReturnValue(equipped.contains(item));
    }
}
