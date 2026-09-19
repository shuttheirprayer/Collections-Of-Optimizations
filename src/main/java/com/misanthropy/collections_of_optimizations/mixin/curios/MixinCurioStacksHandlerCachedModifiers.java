package com.misanthropy.collections_of_optimizations.mixin.curios;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioCachedModifierHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.inventory.CurioStacksHandler;

import java.util.Set;

@Mixin(value = CurioStacksHandler.class, remap = false)
public abstract class MixinCurioStacksHandlerCachedModifiers implements CurioCachedModifierHolder {

    @Shadow
    @Final
    private Set<AttributeModifier> cachedModifiers;

    @Unique
    private boolean coo$loadedCached;

    @Inject(method = "addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V", at = @At("RETURN"), require = 0)
    private void coo$reappliedIsNotCached(AttributeModifier modifier, CallbackInfo ci) {
        if (CoOConfig.curiosReconcileCachedSlotModifiers) {
            this.cachedModifiers.remove(modifier);
        }
    }

    @Inject(method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), require = 0)
    private void coo$keepLoadedCached(CompoundTag nbt, CallbackInfo ci) {
        if (!CoOConfig.curiosReconcileCachedSlotModifiers || !nbt.contains("CachedModifiers", Tag.TAG_LIST)) {
            return;
        }
        ListTag list = nbt.getList("CachedModifiers", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            AttributeModifier modifier = AttributeModifier.load(list.getCompound(i));
            if (modifier != null) {
                this.cachedModifiers.add(modifier);
            }
        }
        this.coo$loadedCached = !this.cachedModifiers.isEmpty();
    }

    @Inject(method = "copyModifiers(Ltop/theillusivec4/curios/api/type/inventory/ICurioStacksHandler;)V", at = @At("RETURN"), require = 0)
    private void coo$markCopiedCached(ICurioStacksHandler other, CallbackInfo ci) {
        if (CoOConfig.curiosReconcileCachedSlotModifiers) {
            this.coo$loadedCached = !this.cachedModifiers.isEmpty();
        }
    }

    @Override
    public boolean coo$skipCachedPurgeOnce() {
        if (this.coo$loadedCached) {
            this.coo$loadedCached = false;
            return true;
        }
        return false;
    }
}
