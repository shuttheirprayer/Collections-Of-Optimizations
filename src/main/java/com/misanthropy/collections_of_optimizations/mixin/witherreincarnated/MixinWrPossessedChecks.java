package com.misanthropy.collections_of_optimizations.mixin.witherreincarnated;

import com.alexander.witherreincarnated.capabilities.possessed.PossessedCapability;
import com.alexander.witherreincarnated.events.HandlePossessedMobsEvent;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.WrState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = HandlePossessedMobsEvent.class, remap = false)
public abstract class MixinWrPossessedChecks {

    @Shadow @Final @Mutable
    public static Predicate<Entity> POSSESSED;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void coo$cheapPossessedCheck(CallbackInfo ci) {
        POSSESSED = WrState::isPossessed;
    }

    @Inject(method = "possessedTick", at = @At("HEAD"), cancellable = true)
    private static void coo$skipIdlePossessedTick(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.wrFastPossessionChecks) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof Mob) {
            PossessedCapability cap = WrState.cap(entity);
            if (cap == null || WrState.isIdle(cap)) {
                ci.cancel();
            }
        }
    }
}
