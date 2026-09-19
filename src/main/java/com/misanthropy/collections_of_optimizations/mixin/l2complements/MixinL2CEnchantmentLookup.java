package com.misanthropy.collections_of_optimizations.mixin.l2complements;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.xkmc.l2complements.events.EnchUtils", remap = false)
public abstract class MixinL2CEnchantmentLookup {

    @Inject(
            method = "getTagEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$fastTagEnchantmentLevel(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.l2complementsLeanEnchantmentLookup || stack == null || enchantment == null) {
            return;
        }
        if (stack.isEmpty()) {
            cir.setReturnValue(0);
            return;
        }

        ListTag list = stack.getEnchantmentTags();
        int size = list.size();
        if (size == 0) {
            cir.setReturnValue(0);
            return;
        }

        ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        if (id == null) {
            return;
        }
        String wanted = id.toString();

        for (int i = 0; i < size; i++) {
            CompoundTag entry = list.getCompound(i);
            String raw = entry.getString("id");
            if (raw.indexOf(':') < 0) {
                return;
            }
            if (raw.equals(wanted)) {
                cir.setReturnValue(Mth.clamp(entry.getInt("lvl"), 0, 255));
                return;
            }
        }

        cir.setReturnValue(0);
    }
}
