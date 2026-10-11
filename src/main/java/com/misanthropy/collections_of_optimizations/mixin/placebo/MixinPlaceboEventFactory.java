package com.misanthropy.collections_of_optimizations.mixin.placebo;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PlaceboEnchantEventProbe;
import dev.shadowsoffire.placebo.events.PlaceboEventFactory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = PlaceboEventFactory.class, remap = false)
public class MixinPlaceboEventFactory {

    @Inject(method = "getEnchantmentLevelSpecific", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void coo$skipSpecificWhenUnheard(int level, IForgeItemStack stack, Enchantment ench, CallbackInfoReturnable<Integer> cir) {
        if (CoOConfig.placeboSkipEmptyEnchantmentEvent && !PlaceboEnchantEventProbe.hasListeners()) {
            cir.setReturnValue(level);
        }
    }

    @Inject(method = "getEnchantmentLevel", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void coo$skipBulkWhenUnheard(Map<Enchantment, Integer> enchantments, IForgeItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> cir) {
        if (CoOConfig.placeboSkipEmptyEnchantmentEvent && !PlaceboEnchantEventProbe.hasListeners()) {
            cir.setReturnValue(new HashMap<>(enchantments));
        }
    }
}
