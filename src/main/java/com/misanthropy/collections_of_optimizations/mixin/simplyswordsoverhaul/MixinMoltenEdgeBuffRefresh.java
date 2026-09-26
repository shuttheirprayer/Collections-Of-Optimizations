package com.misanthropy.collections_of_optimizations.mixin.simplyswordsoverhaul;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.sweenus.simplyswords.item.custom.MoltenEdgeSwordItem", priority = 1500, remap = false)
public abstract class MixinMoltenEdgeBuffRefresh {

    @WrapOperation(
            method = "/modifyInventoryTick$/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            ),
            remap = false,
            require = 0
    )
    private boolean coo$skipFreshBuffRefresh(Player player, MobEffectInstance instance, Operation<Boolean> original) {
        if (!CoOConfig.simplyswordsoverhaulLeanMoltenEdgeBuffs || player == null || instance == null) {
            return original.call(player, instance);
        }
        MobEffectInstance current = player.getEffect(instance.getEffect());
        if (current != null
                && current.getAmplifier() == instance.getAmplifier()
                && current.isAmbient() == instance.isAmbient()
                && current.isVisible() == instance.isVisible()
                && current.showIcon() == instance.showIcon()
                && !current.isInfiniteDuration()
                && current.getDuration() > instance.getDuration() - 20) {
            return false;
        }
        return original.call(player, instance);
    }
}
