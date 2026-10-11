package com.misanthropy.collections_of_optimizations.mixin.subtleeffects;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SubtleEffectsDisplayTick;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientLevel.class, priority = 1500)
public abstract class MixinClientLevelDisplayTick {

    @Unique
    private long coo$displayBiomeAt;

    @Unique
    private Holder<Biome> coo$displayBiome;

    @TargetHandler(
            mixin = "einstein.subtle_effects.mixin.client.ClientLevelMixin",
            name = "animateTick"
    )
    @WrapMethod(method = "@MixinSquared:Handler", require = 0)
    private void coo$planDisplayTick(int x, int y, int z, int range, RandomSource random, Block marker, BlockPos.MutableBlockPos cursor, CallbackInfo ci, Operation<Void> original) {
        if (!CoOConfig.subtleeffectsDisplayTickPlan) {
            this.coo$displayBiome = null;
            original.call(x, y, z, range, random, marker, cursor, ci);
            return;
        }
        this.coo$displayBiomeAt = cursor.asLong();
        this.coo$displayBiome = SubtleEffectsDisplayTick.run((ClientLevel) (Object) this, cursor, random);
    }

    @WrapOperation(
            method = "doAnimateTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"),
            require = 0
    )
    private Holder<Biome> coo$reuseDisplayBiome(ClientLevel level, BlockPos pos, Operation<Holder<Biome>> original) {
        Holder<Biome> biome = this.coo$displayBiome;
        if (biome != null) {
            this.coo$displayBiome = null;
            if (pos.asLong() == this.coo$displayBiomeAt) {
                return biome;
            }
        }
        return original.call(level, pos);
    }
}
