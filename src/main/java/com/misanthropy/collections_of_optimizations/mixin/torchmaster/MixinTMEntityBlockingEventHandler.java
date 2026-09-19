package com.misanthropy.collections_of_optimizations.mixin.torchmaster;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TorchmasterLightRegistry;
import com.misanthropy.collections_of_optimizations.core.TorchmasterRegistryHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.xalcon.torchmaster.common.logic.entityblocking.EntityBlockingEventHandler", remap = false)
public abstract class MixinTMEntityBlockingEventHandler {

    @Inject(method = "shouldBlockEntity", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$useCachedRegistry(Entity entity, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.torchmasterCacheLevelLightRegistry || entity == null) {
            return;
        }
        Level level = entity.level();
        if (!(level instanceof TorchmasterRegistryHolder holder)) {
            return;
        }
        if (!(holder.coo$getTmLightRegistry() instanceof TorchmasterLightRegistry registry)) {
            return;
        }
        if (registry.coo$tmIsEmpty()) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }
        cir.setReturnValue(registry.coo$tmShouldBlockEntity(entity, pos));
    }
}
