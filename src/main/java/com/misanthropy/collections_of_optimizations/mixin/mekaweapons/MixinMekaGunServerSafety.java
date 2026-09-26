package com.misanthropy.collections_of_optimizations.mixin.mekaweapons;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "meranha.mekaweapons.items.ItemMekaGun", remap = false)
public abstract class MixinMekaGunServerSafety {

    @WrapWithCondition(
            method = "m_7203_(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/client/sound/SoundHandler;playSound(Lmekanism/common/registration/impl/SoundEventRegistryObject;)V"
            ),
            require = 0
    )
    private boolean coo$clientOnlyLaserSound(@Coerce Object sound, @Local(argsOnly = true) Level level) {
        return !CoOConfig.mekaweaponsGunClientOnlySound || level.isClientSide;
    }

    @Inject(
            method = "fireLaser(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$serverOnlyLaser(Level level, ItemStack stack, Player owner, Vec3 from, Vec3 to, CallbackInfoReturnable<Vec3> cir) {
        if (CoOConfig.mekaweaponsGunServerOnlyLaser && level.isClientSide) {
            cir.setReturnValue(to);
        }
    }
}
