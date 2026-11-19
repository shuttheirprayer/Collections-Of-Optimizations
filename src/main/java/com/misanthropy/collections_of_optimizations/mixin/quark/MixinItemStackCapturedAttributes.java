package com.misanthropy.collections_of_optimizations.mixin.quark;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.QuarkCapturedAttributes;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = ItemStack.class, priority = 1500)
public abstract class MixinItemStackCapturedAttributes {

    @WrapOperation(
            method = "<init>*",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/ItemStack;capturedAttributes:Ljava/util/Map;", opcode = Opcodes.PUTFIELD, remap = false),
            remap = false
    )
    private void coo$noMapUntilAsked(ItemStack stack, Map<?, ?> map, Operation<Void> original) {
        if (!CoOConfig.quarkLazyCapturedAttributes || !QuarkCapturedAttributes.ready()) {
            original.call(stack, map);
        }
    }

    @Inject(method = "quark$getCapturedAttributes", at = @At("HEAD"), remap = false)
    private void coo$mapForReaders(CallbackInfoReturnable<Map<?, ?>> cir) {
        QuarkCapturedAttributes.ensure((ItemStack) (Object) this);
    }

    @Inject(method = "quark$capturePotionAttributes", at = @At("HEAD"), remap = false)
    private void coo$mapForPotions(List<?> attributes, CallbackInfo ci) {
        QuarkCapturedAttributes.ensure((ItemStack) (Object) this);
    }
}
