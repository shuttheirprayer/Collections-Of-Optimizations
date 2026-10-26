package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AttributeMap.class)
public abstract class MixinAttributeMapFastLookup {

    @Shadow
    @Final
    private Map<Attribute, AttributeInstance> attributes;

    @Shadow
    @Final
    private AttributeSupplier supplier;

    @Inject(
            method = "getInstance(Lnet/minecraft/world/entity/ai/attributes/Attribute;)Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$answerWithoutCompute(Attribute attribute, CallbackInfoReturnable<AttributeInstance> cir) {
        if (!CoOConfig.vanillaFastAttributeInstanceLookup) {
            return;
        }
        AttributeInstance existing = this.attributes.get(attribute);
        if (existing != null) {
            cir.setReturnValue(existing);
            return;
        }
        if (!this.supplier.hasAttribute(attribute)) {
            cir.setReturnValue(null);
        }
    }
}
