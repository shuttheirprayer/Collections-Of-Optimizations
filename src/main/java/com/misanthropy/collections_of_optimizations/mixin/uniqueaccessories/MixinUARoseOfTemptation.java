package com.misanthropy.collections_of_optimizations.mixin.uniqueaccessories;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.genzyuro.uniqueaccessories.system.RoseOfTemptationEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RoseOfTemptationEvents.class, remap = false)
public abstract class MixinUARoseOfTemptation {

    @Inject(method = "onAnyLivingTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnsetPersistentData(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.uniqueaccessoriesSkipUnsetPersistentData) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof EntityPersistentDataAccessor access && access.coo$persistentData() == null) {
            ci.cancel();
        }
    }

    @Inject(method = "cleanupAroundPlayer", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipCleanupScan(ServerLevel level, Player player, double radius, CallbackInfo ci) {
        if (CoOConfig.uniqueaccessoriesSkipRoseCleanupScan) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "onPlayerTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;moveTo(Lnet/minecraft/world/entity/Entity;D)Z",
                    remap = true
            ),
            require = 0
    )
    private static boolean coo$throttleRepath(PathNavigation navigation, Entity target, double speed) {
        int interval = CoOConfig.uniqueaccessoriesRoseRepathInterval;
        if (interval <= 1 || navigation.isDone()
                || Math.floorMod(target.tickCount + System.identityHashCode(navigation), interval) == 0) {
            return navigation.moveTo(target, speed);
        }
        return true;
    }
}
