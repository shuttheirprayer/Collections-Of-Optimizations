package com.misanthropy.collections_of_optimizations.mixin.transmog;

import com.hidoni.transmog.RenderUtils;
import com.hidoni.transmog.TransmogUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FrameStamp;
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
        if (COO$CLIENT && RenderUtils.isCalledForRendering() && coo$transmogged(original)) {
            return TransmogUtils.getAppearanceStackOrOriginal(original);
        }
        return original;
    }

    @Unique
    private static boolean coo$transmogged(ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }
        if (!CoOConfig.transmogFrameMemo) {
            return TransmogUtils.isItemStackTransmogged(stack);
        }
        FrameStamp.TransmogMemo memo = (FrameStamp.TransmogMemo) (Object) stack;
        int frame = FrameStamp.frame;
        if (memo.coo$transmogFrame() == frame) {
            return memo.coo$transmogged();
        }
        boolean transmogged = TransmogUtils.isItemStackTransmogged(stack);
        memo.coo$transmogMemo(frame, transmogged);
        return transmogged;
    }
}
