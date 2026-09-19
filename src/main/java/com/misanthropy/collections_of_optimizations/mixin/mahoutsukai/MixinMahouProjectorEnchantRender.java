package com.misanthropy.collections_of_optimizations.mixin.mahoutsukai;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {"stepsword.mahoutsukai.render.enchant.RenderProjectorEnchant"}, remap = false)
public abstract class MixinMahouProjectorEnchantRender {

    @Inject(method = "renderProjectorEnchantment", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnenchantedArmour(RenderLivingEvent.Pre<?, ?> event, CallbackInfo ci) {
        if (!CoOConfig.mahoutsukaiLeanProjectorEnchantScan || event == null) {
            return;
        }
        Object raw = event.getEntity();
        if (!(raw instanceof LivingEntity living)) {
            return;
        }
        Iterable<ItemStack> armour = living.getArmorSlots();
        if (armour == null) {
            return;
        }
        for (ItemStack stack : armour) {
            if (stack != null && !stack.isEmpty() && stack.getTag() != null) {
                return;
            }
        }
        ci.cancel();
    }
}
