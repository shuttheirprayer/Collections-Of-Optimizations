package com.misanthropy.collections_of_optimizations.mixin.torchmaster;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TorchmasterLightRegistry;
import com.misanthropy.collections_of_optimizations.core.TorchmasterRegistryHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(targets = "net.xalcon.torchmaster.common.logic.entityblocking.LightsRegistryCapability$RegistryContainer", remap = false)
public abstract class MixinTMRegistryContainer implements TorchmasterLightRegistry {

    @Shadow
    private HashMap<String, ?> lights;

    @Shadow
    public abstract boolean shouldBlockEntity(Entity entity, BlockPos pos);

    @Inject(method = "onGlobalTick", at = @At("HEAD"), require = 0)
    private void coo$bindToLevel(Level level, CallbackInfo ci) {
        if (!CoOConfig.torchmasterCacheLevelLightRegistry) {
            return;
        }
        if (level instanceof TorchmasterRegistryHolder holder && holder.coo$getTmLightRegistry() != this) {
            holder.coo$setTmLightRegistry(this);
        }
    }

    @Override
    public boolean coo$tmIsEmpty() {
        HashMap<String, ?> map = this.lights;
        return map == null || map.isEmpty();
    }

    @Override
    public boolean coo$tmShouldBlockEntity(Entity entity, BlockPos pos) {
        return this.shouldBlockEntity(entity, pos);
    }
}
