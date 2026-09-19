package com.misanthropy.collections_of_optimizations.mixin.extrahnn;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.lmor.extrahnn.tile.UltimateSimChamberTileEntity", remap = false)
public abstract class MixinExtraHnnSimChamberResult {

    @ModifyExpressionValue(
            method = "setResult(Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/shadowsoffire/hostilenetworks/data/DataModel;baseDrop()Lnet/minecraft/world/item/ItemStack;"
            ),
            require = 0
    )
    private ItemStack coo$copySharedBaseDrop(ItemStack original) {
        if (!CoOConfig.extrahnnFixSharedBaseDrop || original == null || original.isEmpty()) {
            return original;
        }
        return original.copy();
    }
}
