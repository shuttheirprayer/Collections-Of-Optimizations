package com.misanthropy.collections_of_optimizations.mixin.transmog;

import com.hidoni.transmog.RenderUtils;
import com.hidoni.transmog.TransmogUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Mob.class, Player.class, ArmorStand.class})
public abstract class MixinTransmogEquipment {

    @Unique
    private static final boolean COO$CLIENT = FMLEnvironment.dist.isClient();

    @ModifyReturnValue(method = "getItemBySlot", at = @At("RETURN"))
    private ItemStack coo$transmogAppearance(ItemStack original) {
        if (COO$CLIENT && RenderUtils.isCalledForRendering() && TransmogUtils.isItemStackTransmogged(original)) {
            return TransmogUtils.getAppearanceStackOrOriginal(original);
        }
        return original;
    }
}
