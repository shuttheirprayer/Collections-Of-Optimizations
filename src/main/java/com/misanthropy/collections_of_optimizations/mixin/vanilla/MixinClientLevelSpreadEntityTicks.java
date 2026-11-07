package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.SpreadBlockEntityTicks;
import com.misanthropy.collections_of_optimizations.core.SpreadEntityTicks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevelSpreadEntityTicks {

    @Inject(method = "tickEntities", at = @At("HEAD"))
    private void coo$beginSpreadEntityTicks(CallbackInfo ci) {
        SpreadEntityTicks.beginTick((ClientLevel) (Object) this);
        SpreadBlockEntityTicks.flush();
    }

    @Inject(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tickBlockEntities()V"))
    private void coo$endSpreadEntityTicks(CallbackInfo ci) {
        SpreadEntityTicks.endTick();
    }

    @SuppressWarnings("unchecked")
    @WrapOperation(
            method = "m_194182_",
            remap = false,
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;m_46653_(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/Entity;)V",
                    remap = false
            )
    )
    private void coo$spreadEntityTick(ClientLevel level, Consumer<?> consumer, Entity entity, Operation<Void> original) {
        if (!SpreadEntityTicks.defer(level, (Consumer<Entity>) consumer, entity)) {
            original.call(level, consumer, entity);
        }
    }
}
