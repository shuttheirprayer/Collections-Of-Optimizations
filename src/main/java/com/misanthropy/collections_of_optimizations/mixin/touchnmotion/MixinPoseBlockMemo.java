package com.misanthropy.collections_of_optimizations.mixin.touchnmotion;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LeanRay;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import strm.touchnmotion.ModelSpace;

@Mixin(value = ModelSpace.class, remap = false)
public abstract class MixinPoseBlockMemo {

    @WrapMethod(method = "unturned")
    private static void coo$memoBlocksWhilePosing(LivingEntity entity, PoseStack stack, float partialTick, Consumer<AbstractClientPlayer> model, Operation<Void> original) {
        if (!CoOConfig.touchnmotionBlockMemo) {
            original.call(entity, stack, partialTick, model);
            return;
        }
        LeanRay ray = LeanRay.get();
        ray.beginMemo(entity.getUUID());
        try {
            original.call(entity, stack, partialTick, model);
        } finally {
            ray.endMemo();
        }
    }
}
