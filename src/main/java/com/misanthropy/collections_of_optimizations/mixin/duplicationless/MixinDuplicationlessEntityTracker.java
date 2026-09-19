package com.misanthropy.collections_of_optimizations.mixin.duplicationless;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DuplicationlessTrackerSeed;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.kall.duplicationless.data.AbstractEntityTracker", remap = false)
public abstract class MixinDuplicationlessEntityTracker {

    @Inject(
            method = "update(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;ZLit/unimi/dsi/fastutil/objects/ObjectList;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0)
    private void coo$skipUnqueriedBookkeeping(Entity entity, Level level, boolean add, ObjectList<ResourceLocation> matched, CallbackInfo ci) {
        if (!CoOConfig.duplicationlessLazyEntityTracker) {
            return;
        }
        if (DuplicationlessTrackerSeed.skipUpdate(this, level)) {
            ci.cancel();
        }
    }

    @Inject(method = "chunkSections", at = @At("HEAD"), require = 0)
    private void coo$seedOnFirstQuery(Level level, long chunkPos, CallbackInfoReturnable<Int2ObjectMap<?>> cir) {
        if (!CoOConfig.duplicationlessLazyEntityTracker) {
            return;
        }
        DuplicationlessTrackerSeed.onQuery(this, level);
    }

    @Inject(method = "reset", at = @At("TAIL"), require = 0)
    private void coo$forgetQueriedDimensions(CallbackInfo ci) {
        DuplicationlessTrackerSeed.forget(this);
    }
}
