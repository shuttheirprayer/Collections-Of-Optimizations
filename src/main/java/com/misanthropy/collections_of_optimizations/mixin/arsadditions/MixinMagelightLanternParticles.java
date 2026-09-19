package com.misanthropy.collections_of_optimizations.mixin.arsadditions;

import com.github.jarva.arsadditions.common.block.tile.MagelightLanternTile;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MagelightLanternTile.class, remap = false)
public abstract class MixinMagelightLanternParticles {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$cullDistantLanternParticles(CallbackInfo ci) {
        int radius = CoOConfig.arsadditionsMagelightParticleDistance;
        if (radius <= 0) {
            return;
        }
        BlockEntity self = (BlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || !level.isClientSide) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();
        if (camera == null) {
            return;
        }
        BlockPos pos = self.getBlockPos();
        double dx = camera.getX() - (pos.getX() + 0.5D);
        double dy = camera.getY() - (pos.getY() + 0.5D);
        double dz = camera.getZ() - (pos.getZ() + 0.5D);
        double limit = (double) radius * (double) radius;
        if (dx * dx + dy * dy + dz * dz > limit) {
            ci.cancel();
        }
    }
}
