package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.GuardedEvents;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ForgeHooks.class, remap = false)
public abstract class MixinForgeHooksGuardedLivingTick {

    @WrapOperation(method = "onLivingTick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"), require = 0)
    private static boolean coo$guardedLivingTick(IEventBus bus, Event event, Operation<Boolean> original) {
        return GuardedEvents.post(bus, event);
    }
}
