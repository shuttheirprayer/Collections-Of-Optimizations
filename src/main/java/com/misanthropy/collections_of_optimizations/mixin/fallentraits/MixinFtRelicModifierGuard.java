package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.kayn.fallen_traits.content.item.curio.FuryOfInfernal;
import net.kayn.fallen_traits.content.item.curio.LawOfScale;
import net.kayn.fallen_traits.content.item.curio.TitansHeart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = {TitansHeart.class, FuryOfInfernal.class, LawOfScale.class}, remap = false)
public abstract class MixinFtRelicModifierGuard {

    @Inject(method = "setOrRemoveModifier", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipUnchangedModifier(LivingEntity wearer, Attribute attribute, UUID id, String name, double value,
                                           AttributeModifier.Operation operation, CallbackInfo ci) {
        if (!CoOConfig.fallentraitsSkipUnchangedRelicModifiers) {
            return;
        }
        AttributeInstance instance = wearer.getAttribute(attribute);
        if (instance == null) {
            ci.cancel();
            return;
        }
        AttributeModifier current = instance.getModifier(id);
        if (current == null) {
            if (value == 0.0D) {
                ci.cancel();
            }
        } else if (value != 0.0D && current.getAmount() == value && current.getOperation() == operation
                && name.equals(current.getName())) {
            ci.cancel();
        }
    }
}
