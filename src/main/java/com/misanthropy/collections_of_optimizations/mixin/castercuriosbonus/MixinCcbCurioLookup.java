package com.misanthropy.collections_of_optimizations.mixin.castercuriosbonus;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Pseudo
@Mixin(targets = "com.rinko1231.ccb.utils.MyUtils", remap = false)
public abstract class MixinCcbCurioLookup {

    @Inject(method = "isEquipCurios", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$cachedCurioLookup(LivingEntity entity, Item item, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.castercuriosbonusCachedCurioLookup || entity == null || item == null) {
            return;
        }
        Set<Item> equipped = CurioPresenceCache.equippedItems(entity);
        if (equipped == null) {
            return;
        }
        cir.setReturnValue(equipped.contains(item));
    }
}
