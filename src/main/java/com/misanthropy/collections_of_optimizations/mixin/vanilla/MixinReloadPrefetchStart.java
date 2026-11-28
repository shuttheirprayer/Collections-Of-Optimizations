package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.ReloadPrefetch;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public abstract class MixinReloadPrefetchStart {

    @Shadow
    @Final
    private PackType type;

    @WrapOperation(
            method = "createReload",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"),
            require = 0
    )
    private ReloadInstance coo$startPrefetch(ResourceManager manager, List<PreparableReloadListener> listeners, Executor background,
                                             Executor game, CompletableFuture<Unit> waitingFor, boolean profiled,
                                             Operation<ReloadInstance> original) {
        if (this.type == PackType.CLIENT_RESOURCES) {
            ReloadPrefetch.start(manager, background);
        }
        return original.call(manager, listeners, background, game, waitingFor, profiled);
    }
}
