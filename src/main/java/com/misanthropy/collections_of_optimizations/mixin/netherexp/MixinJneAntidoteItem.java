package com.misanthropy.collections_of_optimizations.mixin.netherexp;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.JneAntidoteEffectCache;
import net.jadenxgamer.netherexp.registry.item.custom.AntidoteItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AntidoteItem.class, remap = false)
public abstract class MixinJneAntidoteItem {

    @Inject(method = "getAntidoteEffect", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$cachedAntidoteEffect(ItemStack stack, CallbackInfoReturnable<MobEffect> cir) {
        if (!CoOConfig.netherexpCacheAntidoteEffect) {
            return;
        }
        cir.setReturnValue(JneAntidoteEffectCache.resolve(stack.getTag()));
    }

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanAntidoteColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.netherexpCacheAntidoteEffect) {
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomAntidoteColor")) {
            cir.setReturnValue(tag.getInt("CustomAntidoteColor"));
            return;
        }
        MobEffect effect = JneAntidoteEffectCache.resolve(tag);
        cir.setReturnValue(effect == null ? 16253176 : effect.getColor());
    }
}
