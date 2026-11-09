package com.misanthropy.collections_of_optimizations.mixin.terramity;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.mcreator.terramity.procedures.PhasingShaderProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PhasingShaderProcedure.class, remap = false)
public abstract class MixinPhasingShaderProcedure {

    @Unique
    private static boolean coo$loadedByTerramity;

    @Inject(
            method = "onPlayerTick",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$localPlayerOnly(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.terramityFixPhasingShaderStomp || event == null || event.player == null) {
            return;
        }
        if (!event.player.level().isClientSide()) {

            ci.cancel();
            return;
        }
        if (event.player != Minecraft.getInstance().player) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;loadEffect(Lnet/minecraft/resources/ResourceLocation;)V",
                    remap = true
            ),
            remap = true,
            require = 0
    )
    private static void coo$rememberOurEffect(GameRenderer renderer, ResourceLocation effect, Operation<Void> original) {
        original.call(renderer, effect);
        coo$loadedByTerramity = true;
    }

    @WrapWithCondition(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownEffect()V",
                    remap = true
            ),
            remap = true,
            require = 0
    )
    private static boolean coo$onlyShutDownOurEffect(GameRenderer renderer) {
        if (!CoOConfig.terramityFixPhasingShaderStomp) {
            return true;
        }
        if (!coo$loadedByTerramity) {
            return false;
        }
        coo$loadedByTerramity = false;
        return true;
    }
}
