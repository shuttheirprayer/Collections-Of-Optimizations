package com.misanthropy.collections_of_optimizations.mixin.cognition;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.cyanogen.experienceobelisk.event.DescriptionTooltips", remap = false)
public abstract class MixinCognitionDescriptionTooltips {

    @Inject(method = "handleTooltip", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipForeignTooltips(ItemTooltipEvent event, CallbackInfo ci) {
        if (!CoOConfig.cognitionSkipForeignTooltipScan || event == null) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!"experienceobelisk".equals(stack.getItem().getCreatorModId(stack))) {
            ci.cancel();
        }
    }
}
