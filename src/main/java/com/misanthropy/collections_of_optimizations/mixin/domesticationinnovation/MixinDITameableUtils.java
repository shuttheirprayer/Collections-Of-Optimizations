package com.misanthropy.collections_of_optimizations.mixin.domesticationinnovation;

import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe668.domesticationinnovation.DomesticationMod;
import com.github.alexthe668.domesticationinnovation.server.entity.TameableUtils;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DomesticationEnchantCache;
import com.misanthropy.collections_of_optimizations.core.DomesticationHealers;
import com.misanthropy.collections_of_optimizations.core.DomesticationTameable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = TameableUtils.class, remap = false)
public abstract class MixinDITameableUtils {

    @Inject(method = "getEnchantLevel", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$fastEnchantLevel(LivingEntity entity, Enchantment enchantment, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.domesticationinnovationFastEnchantLookup || entity == null || enchantment == null) {
            return;
        }
        CompoundTag tag = CitadelEntityData.getCitadelTag(entity);
        if (tag == null || !tag.contains("StoredPetEnchantments")) {
            cir.setReturnValue(0);
            return;
        }
        String key = DomesticationEnchantCache.keyOf(enchantment);
        if (key == null || !DomesticationMod.CONFIG.isEnchantEnabled(enchantment)) {
            cir.setReturnValue(0);
            return;
        }
        ListTag list = tag.getList("StoredPetEnchantments", 10);
        cir.setReturnValue(DomesticationEnchantCache.levelOf(list, key));
    }

    @Inject(method = "isTamed", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUntameableTypes(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.domesticationinnovationSkipImpossibleTamedCheck) {
            return;
        }
        if (entity == null || !DomesticationTameable.canEverBeTamed(entity.getClass())) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @Inject(method = "getNearbyHealers", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$indexedHealers(LivingEntity hurtOwner, CallbackInfoReturnable<List<LivingEntity>> cir) {
        if (CoOConfig.domesticationinnovationIndexedHealers) {
            cir.setReturnValue(DomesticationHealers.nearby(hurtOwner));
        }
    }

    @Inject(method = "setEnchantmentTag", at = @At("HEAD"), require = 0)
    private static void coo$invalidateEnchantCache(LivingEntity entity, ListTag list, CallbackInfo ci) {
        DomesticationEnchantCache.invalidate();
    }
}
