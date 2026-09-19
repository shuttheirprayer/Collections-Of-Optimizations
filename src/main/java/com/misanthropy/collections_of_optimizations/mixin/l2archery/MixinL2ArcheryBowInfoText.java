package com.misanthropy.collections_of_optimizations.mixin.l2archery;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2ArcheryClientCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "dev.xkmc.l2archery.content.client.BowInfoOverlay", remap = false)
public abstract class MixinL2ArcheryBowInfoText {

    @Inject(
            method = "getText()Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$cachedInfoText(CallbackInfoReturnable<List<Component>> cir) {
        if (!CoOConfig.l2archeryCacheBowInfoText) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        ItemStack bow = player.getMainHandItem();
        List<Component> cached = L2ArcheryClientCache.infoText(bow, player.getProjectile(bow));
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "getText()Ljava/util/List;",
            at = @At("RETURN"),
            require = 0
    )
    private void coo$storeInfoText(CallbackInfoReturnable<List<Component>> cir) {
        if (!CoOConfig.l2archeryCacheBowInfoText) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        ItemStack bow = player.getMainHandItem();
        L2ArcheryClientCache.putInfoText(bow, player.getProjectile(bow), cir.getReturnValue());
    }
}
