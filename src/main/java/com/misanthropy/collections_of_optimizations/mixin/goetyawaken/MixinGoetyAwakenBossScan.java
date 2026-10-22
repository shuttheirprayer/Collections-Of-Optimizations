package com.misanthropy.collections_of_optimizations.mixin.goetyawaken;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CameraShakeScanCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "com.k1sak1.goetyawaken.client.ClientEvents", remap = false)
public abstract class MixinGoetyAwakenBossScan {

    @WrapOperation(
            method = {"onRenderFog", "onGetFogColor", "onRenderLevelStage"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;",
                    remap = true
            ),
            require = 0
    )
    private static List<Entity> coo$scanOncePerTick(ClientLevel level, Class<Entity> type, AABB box, Operation<List<Entity>> original) {
        if (!CoOConfig.goetyawakenScanOncePerTick) {
            return original.call(level, type, box);
        }
        return CameraShakeScanCache.get(level, type, box, original);
    }
}
