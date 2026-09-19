package com.misanthropy.collections_of_optimizations.mixin.lethality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import com.misanthropy.collections_of_optimizations.core.LethalityIds;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Pseudo
@Mixin(targets = "net.daphne.lethality.events.CommonForgeEvents", remap = false)
public abstract class MixinLethalityCharmScan {

    @Inject(method = "onEntityHurt", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnequippedCharmScan(LivingHurtEvent event, CallbackInfo ci) {
        if (!CoOConfig.lethalitySkipUnequippedCharmScan || event == null) {
            return;
        }
        Entity direct = event.getSource().getDirectEntity();
        if (!(direct instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide) {
            return;
        }
        Set<Item> equipped = CurioPresenceCache.equippedItems(player);
        if (equipped == null) {
            return;
        }
        if (!equipped.isEmpty()) {
            for (Item item : LethalityIds.charmItems()) {
                if (equipped.contains(item)) {
                    return;
                }
            }
        }
        ci.cancel();
    }
}
