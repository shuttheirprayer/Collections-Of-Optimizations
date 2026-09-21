package com.misanthropy.collections_of_optimizations.mixin.puffishattributes;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.puffish.attributesmod.api.DynamicEntityAttribute;
import net.puffish.attributesmod.api.DynamicModification;
import net.puffish.attributesmod.util.DynamicModificationImpl;
import net.puffish.attributesmod.util.Sign;
import net.puffish.attributesmod.util.Signed;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = DynamicModificationImpl.class, remap = false)
public abstract class MixinPuffishDynamicModification {

    @Shadow
    @Final
    private List<Signed<AttributeInstance>> attributes;

    @Inject(
            method = "with(Lnet/puffish/attributesmod/util/Sign;Lnet/minecraft/world/entity/ai/attributes/Attribute;Lnet/minecraft/world/entity/LivingEntity;)Lnet/puffish/attributesmod/api/DynamicModification;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipUnmodifiedAttribute(Sign sign, Attribute attribute, LivingEntity entity, CallbackInfoReturnable<DynamicModification> cir) {
        if (!CoOConfig.puffishattributesSkipEmptyModifiers || attribute.getClass() != DynamicEntityAttribute.class) {
            return;
        }
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && !instance.getModifiers().isEmpty()) {
            return;
        }
        cir.setReturnValue((DynamicModification) (Object) this);
    }

    @Inject(
            method = "applyTo(D)D",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$applyNothing(double value, CallbackInfoReturnable<Double> cir) {
        if (CoOConfig.puffishattributesSkipEmptyModifiers && this.attributes.isEmpty()) {
            cir.setReturnValue(value);
        }
    }
}
