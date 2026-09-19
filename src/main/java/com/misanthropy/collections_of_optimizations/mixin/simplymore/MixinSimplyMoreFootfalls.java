package com.misanthropy.collections_of_optimizations.mixin.simplymore;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.rosemarythyme.simplymore.util.SimplyMoreHelperMethods", remap = false)
public abstract class MixinSimplyMoreFootfalls {

    @Unique
    private static boolean coo$footfallsAreDead(Entity entity, ItemStack stack) {
        if (!CoOConfig.simplymoreSkipUnheldFootfalls) {
            return false;
        }
        if (!(entity instanceof Player player)) {
            return true;
        }
        return player.getItemBySlot(EquipmentSlot.MAINHAND) != stack;
    }

    @Unique
    private static int coo$advanceStep(int stepMod) {
        return stepMod > 0 ? stepMod - 1 : 7;
    }

    @Inject(
            method = "simplyMore$footfallsHelper(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;ILnet/minecraft/core/particles/SimpleParticleType;)I",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipUnheldFootfalls(Entity entity, ItemStack stack, Level level, int stepMod,
                                                SimpleParticleType particle, CallbackInfoReturnable<Integer> cir) {
        if (coo$footfallsAreDead(entity, stack)) {
            cir.setReturnValue(coo$advanceStep(stepMod));
        }
    }

    @Inject(
            method = "simplyMore$footfallsHelper(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;ILnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/core/particles/SimpleParticleType;)I",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipUnheldFootfallsTriple(Entity entity, ItemStack stack, Level level, int stepMod,
                                                      SimpleParticleType first, SimpleParticleType second,
                                                      SimpleParticleType third, CallbackInfoReturnable<Integer> cir) {
        if (coo$footfallsAreDead(entity, stack)) {
            cir.setReturnValue(coo$advanceStep(stepMod));
        }
    }
}
