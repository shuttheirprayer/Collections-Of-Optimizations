package com.misanthropy.collections_of_optimizations.mixin.undergarden;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "quek.undergarden.item.tool.UGToolEvents", remap = false)
public abstract class MixinUgForgottenToolRegistryKeys {

    @WrapOperation(
            method = {"forgottenAttackEvent", "forgottenDigEvent"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/registries/IForgeRegistry;getKey(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            remap = false,
            require = 0
    )
    private static ResourceLocation coo$tolerateUnregisteredKey(IForgeRegistry registry, Object value, Operation<ResourceLocation> original) {
        ResourceLocation key = original.call(registry, value);
        if (key == null && CoOConfig.undergardenTolerateUnregisteredForgottenTargets) {
            return new ResourceLocation("minecraft", "air");
        }
        return key;
    }
}
