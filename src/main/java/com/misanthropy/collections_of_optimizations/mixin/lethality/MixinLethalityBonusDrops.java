package com.misanthropy.collections_of_optimizations.mixin.lethality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LethalityIds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.daphne.lethality.events.CommonForgeEvents", remap = false)
public abstract class MixinLethalityBonusDrops {

    @Inject(method = "onEntityDrop", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipForeignDropChecks(LivingDropsEvent event, CallbackInfo ci) {
        if (!CoOConfig.lethalityLeanBonusDropCheck || event == null) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) {
            ci.cancel();
            return;
        }
        if (!LethalityIds.bonusDropTypes().contains(entity.getType())) {
            ci.cancel();
        }
    }
}
