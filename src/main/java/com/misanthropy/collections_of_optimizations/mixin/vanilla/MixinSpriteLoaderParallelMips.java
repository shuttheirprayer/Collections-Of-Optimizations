package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ParallelMips;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public abstract class MixinSpriteLoaderParallelMips {

    @Unique
    private CompletableFuture<Void> coo$deferred;

    @Unique
    private Executor coo$executor;

    @WrapOperation(
            method = "stitch",
            at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;runAsync(Ljava/lang/Runnable;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"),
            require = 1
    )
    private CompletableFuture<Void> coo$deferMipmaps(Runnable task, Executor executor, Operation<CompletableFuture<Void>> original) {
        if (!CoOConfig.vanillaParallelMipmaps) {
            return original.call(task, executor);
        }
        this.coo$executor = executor;
        this.coo$deferred = CompletableFuture.completedFuture(null);
        return this.coo$deferred;
    }

    @WrapOperation(
            method = "stitch",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/texture/SpriteLoader$Preparations;"),
            require = 1
    )
    private SpriteLoader.Preparations coo$generateMipmaps(int width, int height, int mipLevel, TextureAtlasSprite missing,
                                                          Map<ResourceLocation, TextureAtlasSprite> regions, CompletableFuture<Void> ready,
                                                          Operation<SpriteLoader.Preparations> original) {
        if (ready != null && ready == this.coo$deferred) {
            ready = ParallelMips.generate(regions, mipLevel, this.coo$executor);
        }
        this.coo$deferred = null;
        this.coo$executor = null;
        return original.call(width, height, mipLevel, missing, regions, ready);
    }
}
