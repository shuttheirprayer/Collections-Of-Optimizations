package com.misanthropy.collections_of_optimizations.mixin.somakespells;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.somakespells.event.ConnectionEventHandler", remap = false)
public abstract class MixinSomakeConnectionTick {

    @Inject(method = "onLivingTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnlinkedEntities(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.somakespellsSkipUnlinkedConnectionTick || event == null) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel)) {
            ci.cancel();
            return;
        }
        CompoundTag data = entity.getPersistentData();
        if (data.contains("SomakeConnectionLink", Tag.TAG_COMPOUND)
                || data.contains("SomakeConnectionTransferQueue", Tag.TAG_COMPOUND)) {
            return;
        }
        if (entity instanceof Player && data.contains("SomakeConnectionPendingCast", Tag.TAG_COMPOUND)) {
            return;
        }
        ci.cancel();
    }
}
