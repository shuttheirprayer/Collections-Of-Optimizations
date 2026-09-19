package com.misanthropy.collections_of_optimizations.mixin.meetyourfight;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Pseudo
@Mixin(targets = "lykrast.meetyourfight.misc.EventHandler", remap = false)
public abstract class MixinMyfCurioLookup {

    @Inject(method = "isWearing", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$cachedCurioLookup(LivingEntity entity, RegistryObject<Item> holder, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.meetyourfightCachedCurioLookup || entity == null || holder == null) {
            return;
        }
        Item item = holder.orElse(null);
        if (item == null) {
            return;
        }
        Set<Item> equipped = CurioPresenceCache.equippedItems(entity);
        if (equipped == null) {
            return;
        }
        cir.setReturnValue(equipped.contains(item));
    }
}
