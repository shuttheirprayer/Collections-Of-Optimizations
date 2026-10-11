package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FtModifierIds;
import net.kayn.fallen_traits.content.traits.basic.DaywalkerTrait;
import net.kayn.fallen_traits.content.traits.basic.NightcrawlerTrait;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {DaywalkerTrait.class, NightcrawlerTrait.class}, remap = false)
public abstract class MixinFtDayNightModifierGuard {

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/xkmc/l2hostility/content/logic/TraitManager;addAttribute(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/attributes/Attribute;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)V"
            ),
            require = 0
    )
    private boolean coo$skipUnchangedModifier(LivingEntity entity, Attribute attribute, String name, double factor,
                                              AttributeModifier.Operation operation) {
        if (!CoOConfig.fallentraitsSkipUnchangedDayNightModifiers) {
            return true;
        }
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return false;
        }
        AttributeModifier current = instance.getModifier(FtModifierIds.of(name));
        return current == null || current.getAmount() != factor || current.getOperation() != operation
                || !name.equals(current.getName());
    }
}
