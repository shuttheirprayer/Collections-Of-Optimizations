package com.misanthropy.collections_of_optimizations.mixin.mutantmonsters;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "fuzs.mutantmonsters.handler.PlayerEventsHandler", remap = false)
public abstract class MixinMutantMonstersShoulderSound {

    @Inject(
            method = "playShoulderEntitySound",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipEmptyShoulderTag(Player player, CompoundTag tag, CallbackInfo ci) {
        if (!CoOConfig.mutantmonstersSkipEmptyShoulderLookup) {
            return;
        }
        if (tag == null || tag.isEmpty()) {
            ci.cancel();
        }
    }
}
