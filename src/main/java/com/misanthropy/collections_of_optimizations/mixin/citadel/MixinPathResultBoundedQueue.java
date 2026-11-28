package com.misanthropy.collections_of_optimizations.mixin.citadel;

import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.PathFindingStatus;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.PathResult;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.pathjobs.AbstractPathJob;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CitadelPathQueue;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Mixin(value = PathResult.class, remap = false)
public abstract class MixinPathResultBoundedQueue {

    @Inject(method = "startJob", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$refuseWhenBacklogged(ExecutorService service, CallbackInfo ci) {
        if (!CoOConfig.citadelBoundedPathQueue) {
            return;
        }
        PathResult<?> self = (PathResult<?>) (Object) this;
        if (!(self.getJob() instanceof AbstractPathJob job)) {
            return;
        }
        LivingEntity entity = ((CitadelPathJobAccess) job).coo$entity().get();
        UUID id = entity == null ? null : entity.getUUID();
        boolean cooling = CitadelPathQueue.cooling(id);
        if (cooling || CitadelPathQueue.full(service)) {
            if (!cooling) {
                CitadelPathQueue.reject(id);
            }
            self.setStatus(PathFindingStatus.CANCELLED);
            self.cancel();
            ci.cancel();
        }
    }
}
