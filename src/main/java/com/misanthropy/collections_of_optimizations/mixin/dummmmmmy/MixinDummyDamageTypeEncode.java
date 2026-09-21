package com.misanthropy.collections_of_optimizations.mixin.dummmmmmy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DummyDamageNumbers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Pseudo
@Mixin(targets = "net.mehvahdjukaar.dummmmmmy.network.ClientBoundDamageNumberMessage", remap = false)
public abstract class MixinDummyDamageTypeEncode {

    @Inject(
            method = "encodeDamage(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void coo$tolerateUnregisteredDamageType(Level level, DamageSource source, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!CoOConfig.dummmmmmyTolerateUnregisteredDamageTypes || source == null) {
            return;
        }
        Optional<ResourceKey<DamageType>> key;
        try {
            key = source.typeHolder().unwrapKey();
        } catch (Throwable throwable) {
            cir.setReturnValue(DummyDamageNumbers.trueDamage());
            return;
        }
        if (key != null && key.isPresent()) {
            cir.setReturnValue(key.get().location());
        } else {
            cir.setReturnValue(DummyDamageNumbers.trueDamage());
        }
    }
}
