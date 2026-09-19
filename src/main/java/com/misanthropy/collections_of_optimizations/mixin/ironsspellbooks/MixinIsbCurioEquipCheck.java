package com.misanthropy.collections_of_optimizations.mixin.ironsspellbooks;

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
@Mixin(targets = "io.redspace.ironsspellbooks.item.curios.CurioBaseItem", remap = false)
public abstract class MixinIsbCurioEquipCheck {

    @Inject(method = "isEquippedBy", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$cachedCurioLookup(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.ironsspellbooksLeanCurioEquipCheck || entity == null) {
            return;
        }
        Set<Item> equipped = CurioPresenceCache.equippedItems(entity);
        if (equipped == null) {
            return;
        }
        cir.setReturnValue(equipped.contains((Item) (Object) this));
    }
}
