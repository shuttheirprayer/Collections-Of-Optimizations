package com.misanthropy.collections_of_optimizations.mixin.celestialcore;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CelestialCoreTransformScan;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemEntity.class, priority = 1500, remap = false)
public abstract class MixinCCItemEntityTransform {

    @Inject(method = "celestial_core$tick$recipe", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipTransformationScan(CallbackInfo ci) {
        if (!CoOConfig.celestialcoreLeanItemTransformScan) {
            return;
        }
        ItemEntity self = (ItemEntity) (Object) this;
        Level level = self.level();
        if (level == null || level.isClientSide() || self.isRemoved()) {
            return;
        }
        if (!CelestialCoreTransformScan.mayTransform(level, self, CoOConfig.celestialcoreMatchStatBlockRecipe)) {
            ci.cancel();
        }
    }

    @Inject(method = "celestial_core$tick$recipe", at = @At("RETURN"), require = 0)
    private void coo$clearTransformationScan(CallbackInfo ci) {
        CelestialCoreTransformScan.disarm();
    }
}
