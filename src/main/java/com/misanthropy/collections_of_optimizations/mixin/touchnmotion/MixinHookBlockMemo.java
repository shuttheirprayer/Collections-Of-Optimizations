package com.misanthropy.collections_of_optimizations.mixin.touchnmotion;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LeanRay;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import strm.touchnmotion.TouchNMotionHook;

@Mixin(value = TouchNMotionHook.class, remap = false)
public abstract class MixinHookBlockMemo {

    @WrapMethod(method = "applyAllParts")
    private static void coo$memoBlocksWhileApplying(UUID uuid, Function<String, ModelPart> parts, boolean player, boolean mainModel, Operation<Void> original) {
        if (!CoOConfig.touchnmotionBlockMemo) {
            original.call(uuid, parts, player, mainModel);
            return;
        }
        LeanRay ray = LeanRay.get();
        ray.beginMemo(uuid);
        try {
            original.call(uuid, parts, player, mainModel);
        } finally {
            ray.endMemo();
        }
    }
}
