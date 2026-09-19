package com.misanthropy.collections_of_optimizations.mixin.hostilenetworks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.HnnModelHolderCache;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.shadowsoffire.hostilenetworks.item.DataModelItem", remap = false)
public abstract class MixinHnnDataModelHolder {

    @Inject(
            method = "getStoredModel",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$readCachedModelHolder(ItemStack stack, CallbackInfoReturnable<Object> cir) {
        if (!CoOConfig.hostilenetworksCacheModelHolder) {
            return;
        }
        String id = HnnModelHolderCache.keyOf(stack);
        if (id == null) {
            return;
        }
        Object holder = HnnModelHolderCache.get(id);
        if (holder != null) {
            cir.setReturnValue(holder);
        }
    }

    @Inject(
            method = "getStoredModel",
            at = @At("RETURN"),
            require = 0
    )
    private static void coo$storeModelHolder(ItemStack stack, CallbackInfoReturnable<Object> cir) {
        if (!CoOConfig.hostilenetworksCacheModelHolder) {
            return;
        }
        String id = HnnModelHolderCache.keyOf(stack);
        if (id == null) {
            return;
        }
        HnnModelHolderCache.put(id, cir.getReturnValue());
    }
}
