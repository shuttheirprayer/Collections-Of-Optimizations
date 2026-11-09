package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelPart.class)
public abstract class MixinModelPartRotationReuse {

    @Unique
    private static final Quaternionf coo$rotation = new Quaternionf();

    @WrapOperation(method = "translateAndRotate", at = @At(value = "NEW", target = "()Lorg/joml/Quaternionf;"), require = 0)
    private Quaternionf coo$reuseRotation(Operation<Quaternionf> original) {
        if (CoOConfig.vanillaReuseModelPartRotation && RenderSystem.isOnRenderThread()) {
            return coo$rotation;
        }
        return original.call();
    }
}
