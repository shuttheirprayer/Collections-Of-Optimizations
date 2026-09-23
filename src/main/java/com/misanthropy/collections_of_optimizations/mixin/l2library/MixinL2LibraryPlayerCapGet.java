package com.misanthropy.collections_of_optimizations.mixin.l2library;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.capability.player.PlayerCapabilityTemplate;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerCapabilityHolder.class, remap = false)
public abstract class MixinL2LibraryPlayerCapGet {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(
            method = "get(Lnet/minecraft/world/entity/player/Player;)Ldev/xkmc/l2library/capability/player/PlayerCapabilityTemplate;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$resolveCapabilityOnce(Player player, CallbackInfoReturnable<PlayerCapabilityTemplate> cir) {
        if (!CoOConfig.l2libraryLeanCapabilityResolve || player == null) {
            return;
        }
        PlayerCapabilityHolder<?> self = (PlayerCapabilityHolder<?>) (Object) this;
        Object data = player.getCapability(self.capability).orElse(null);
        if (data instanceof PlayerCapabilityTemplate<?> template) {
            cir.setReturnValue(template.check());
        }
    }
}
