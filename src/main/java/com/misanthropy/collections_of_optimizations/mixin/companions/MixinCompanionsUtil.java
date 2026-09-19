package com.misanthropy.collections_of_optimizations.mixin.companions;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Util.class, remap = false)
public abstract class MixinCompanionsUtil {

    @Inject(
            method = "computePlayer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/entity/player/Player;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$fastOwnerWalk(Entity entity, CallbackInfoReturnable<Player> cir) {
        if (!CoOConfig.companionsFastOwnerWalk) {
            return;
        }

        Entity current = entity;
        for (int depth = 0; depth < 32 && current != null; depth++) {
            if (current instanceof Player player) {
                cir.setReturnValue(player);
                return;
            }
            if (current instanceof Projectile projectile) {
                current = projectile.getOwner();
                continue;
            }
            if (current instanceof OwnableEntity ownable) {
                current = ownable.getOwner();
                continue;
            }
            break;
        }

        cir.setReturnValue(null);
    }
}
