package com.misanthropy.collections_of_optimizations.mixin.ironslib;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.core.ArmorModelBake;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "io.redspace.ironslib.patreon.transmog.TransmogClientHandler", remap = false)
public abstract class MixinTransmogOuterLayerBake {

    @WrapMethod(
            method = "shouldDisableOuterLayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/EquipmentSlot;)Z",
            require = 0
    )
    private static boolean coo$markArmorModelProbe(Player player, EquipmentSlot slot, Operation<Boolean> original) {
        ArmorModelBake.enter();
        try {
            return original.call(player, slot);
        } finally {
            ArmorModelBake.exit();
        }
    }
}
