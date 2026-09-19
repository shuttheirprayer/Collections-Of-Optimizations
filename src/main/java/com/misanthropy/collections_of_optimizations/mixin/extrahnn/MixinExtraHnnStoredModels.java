package com.misanthropy.collections_of_optimizations.mixin.extrahnn;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ExtraHnnModelListCache;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "net.lmor.extrahnn.item.ExtraDataModelItem", remap = false)
public abstract class MixinExtraHnnStoredModels {

    @Inject(
            method = "getStoredModels(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$readCachedStoredModels(ItemStack stack, CallbackInfoReturnable<List<Object>> cir) {
        if (!CoOConfig.extrahnnCacheStoredModels) {
            return;
        }

        ListTag key = ExtraHnnModelListCache.idList(stack);
        if (key == null) {
            return;
        }

        List<Object> cached = ExtraHnnModelListCache.get(key);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "getStoredModels(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            require = 0
    )
    private static void coo$storeStoredModels(ItemStack stack, CallbackInfoReturnable<List<Object>> cir) {
        if (!CoOConfig.extrahnnCacheStoredModels) {
            return;
        }

        ListTag key = ExtraHnnModelListCache.idList(stack);
        if (key != null) {
            ExtraHnnModelListCache.put(key, cir.getReturnValue());
        }
    }
}
