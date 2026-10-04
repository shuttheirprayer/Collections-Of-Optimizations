package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.init.ModCapabilities;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CataclysmCapHolder;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ModCapabilities.class, remap = false)
public abstract class MixinCataclysmCapabilityLookup {

    @WrapMethod(method = "getCapability", require = 0)
    private static Object coo$cachedCapability(Entity entity, Capability<?> capability, Operation<Object> original) {
        if (!CoOConfig.cataclysmCacheCapabilities || !(entity instanceof CataclysmCapHolder holder) || !entity.isAlive()) {
            return original.call(entity, capability);
        }
        int index = coo$slotFor(capability);
        if (index < 0) {
            return original.call(entity, capability);
        }
        Object cached = holder.coo$getCataclysmCap(index);
        if (cached == CataclysmCapHolder.COO_ABSENT) {
            return null;
        }
        if (cached != null) {
            return cached;
        }
        Object resolved = original.call(entity, capability);
        holder.coo$setCataclysmCap(index, resolved == null ? CataclysmCapHolder.COO_ABSENT : resolved);
        return resolved;
    }

    @Unique
    private static int coo$slotFor(Capability<?> capability) {
        if (capability == ModCapabilities.HOOK_CAPABILITY) {
            return 0;
        }
        if (capability == ModCapabilities.CHARGE_CAPABILITY) {
            return 1;
        }
        if (capability == ModCapabilities.RENDER_RUSH_CAPABILITY) {
            return 2;
        }
        if (capability == ModCapabilities.TENTACLE_CAPABILITY) {
            return 3;
        }
        if (capability == ModCapabilities.PARRY_CAPABILITY) {
            return 4;
        }
        return -1;
    }
}
