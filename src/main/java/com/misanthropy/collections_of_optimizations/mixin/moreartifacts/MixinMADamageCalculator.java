package com.misanthropy.collections_of_optimizations.mixin.moreartifacts;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.MoreArtifactsEquipState;
import net.gobies.moreartifacts.util.DamageCalculator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = DamageCalculator.class, remap = false)
public abstract class MixinMADamageCalculator {

    @Inject(method = "getCurrentEquipState", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanEquipState(Player player, CallbackInfoReturnable<Map<Item, Boolean>> cir) {
        if (!CoOConfig.moreartifactsFastEquipState) {
            return;
        }
        Map<Item, Boolean> state = MoreArtifactsEquipState.of(player);
        if (state != null) {
            cir.setReturnValue(state);
        }
    }
}
