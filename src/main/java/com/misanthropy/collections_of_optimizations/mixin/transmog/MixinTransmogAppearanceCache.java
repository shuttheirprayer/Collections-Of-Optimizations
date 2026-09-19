package com.misanthropy.collections_of_optimizations.mixin.transmog;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TransmogAppearanceCache;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.hidoni.transmog.TransmogUtils", remap = false)
public abstract class MixinTransmogAppearanceCache {

    @Inject(
            method = "getAppearanceItemStack(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$identityAppearanceCache(ItemStack stack, boolean includeHidden, CallbackInfoReturnable<ItemStack> cir) {
        if (!CoOConfig.transmogIdentityAppearanceCache) {
            return;
        }

        ItemStack appearance = TransmogAppearanceCache.resolve(stack);
        if (appearance == null) {
            return;
        }

        if (!includeHidden && TransmogAppearanceCache.isHidden(appearance)) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        cir.setReturnValue(appearance);
    }
}
