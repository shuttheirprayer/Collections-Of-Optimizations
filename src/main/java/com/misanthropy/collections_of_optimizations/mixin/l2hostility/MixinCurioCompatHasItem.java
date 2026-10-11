package com.misanthropy.collections_of_optimizations.mixin.l2hostility;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2CurioSlotTags;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CurioCompat.class, remap = false)
public abstract class MixinCurioCompatHasItem {

    @Inject(method = "hasItemImpl", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanHasItem(LivingEntity entity, Item item, CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.l2hostilityLeanCurioItemCheck) {
            cir.setReturnValue(L2CurioSlotTags.hasItem(entity, item));
        }
    }
}
