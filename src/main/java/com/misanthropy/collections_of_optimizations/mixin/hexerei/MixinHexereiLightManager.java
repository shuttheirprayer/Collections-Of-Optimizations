package com.misanthropy.collections_of_optimizations.mixin.hexerei;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.HexereiLightState;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.joefoxe.hexerei.light.LightManager", remap = false)
public abstract class MixinHexereiLightManager {

    @WrapMethod(method = "shouldUpdateDynamicLight", require = 0)
    private static boolean coo$cachedToggle(Operation<Boolean> original) {
        if (CoOConfig.hexereiDisableDynamicLights) {
            return false;
        }
        if (!CoOConfig.hexereiCacheLightToggle) {
            return original.call();
        }
        int state = HexereiLightState.cachedToggle();
        if (state != 0) {
            return HexereiLightState.toggleIsOn(state);
        }
        boolean value = original.call();
        HexereiLightState.rememberToggle(value);
        return value;
    }

    @Inject(method = "updateAll", at = @At("HEAD"), require = 0)
    private static void coo$dropToggleOnFrame(LevelRenderer renderer, CallbackInfo ci) {
        if (CoOConfig.hexereiDisableDynamicLights) {
            HexereiLightState.clearSources();
            return;
        }
        HexereiLightState.dropToggle();
    }

    @Inject(method = "toggleLightsAndConfig", at = @At("RETURN"), require = 0)
    private static void coo$dropToggleOnSwitch(boolean value, CallbackInfo ci) {
        HexereiLightState.dropToggle();
    }

    @WrapMethod(method = "getDynamicLightLevel", require = 0)
    private static double coo$skipEmptyLightScan(BlockPos pos, Operation<Double> original) {
        return coo$idle() ? 0.0D : original.call(pos);
    }

    @WrapMethod(method = "getDynamicLightLevelWorld", require = 0)
    private static double coo$skipEmptyWorldLightScan(BlockPos pos, Operation<Double> original) {
        return coo$idle() ? 0.0D : original.call(pos);
    }

    @WrapMethod(method = "getLightmapWithDynamicLight(Lnet/minecraft/core/BlockPos;I)I", require = 0)
    private static int coo$skipEmptyLightmap(BlockPos pos, int lightmap, Operation<Integer> original) {
        return coo$idle() ? lightmap : original.call(pos, lightmap);
    }

    @Unique
    private static boolean coo$idle() {
        return CoOConfig.hexereiDisableDynamicLights
                || (CoOConfig.hexereiSkipEmptyLightScan && HexereiLightState.noSources());
    }
}
