package com.misanthropy.collections_of_optimizations.mixin.elysiumapi;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ElysiumReplacement;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.worldgen.IExtendedParameterList;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 996)
public abstract class MixinMultiNoiseBiomeSourceTerrablender {

    @Shadow
    private Climate.ParameterList<Holder<Biome>> parameters() {
        throw new AssertionError();
    }

    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void coo$replaceOnce(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if (!(this instanceof ElysiumBiomeSource source) || source.getDimension() == null) {
            return;
        }
        RegistryAccess access = ElysiumReplacement.access();
        if (access == null) {
            return;
        }
        Climate.ParameterList<Holder<Biome>> parameters = parameters();
        boolean terrablender = parameters instanceof IExtendedParameterList<?> extended && extended.isInitialized();
        Climate.TargetPoint target = sampler.sample(x, y, z);
        @SuppressWarnings("unchecked")
        Holder<Biome> current = terrablender
                ? ((IExtendedParameterList<Holder<Biome>>) parameters).findValuePositional(target, x, y, z)
                : parameters.findValue(target);
        if (current == null) {
            return;
        }
        Holder<Biome> replaced = ElysiumReplacement.replace(x, z, current, source.getDimension(), source.getWorldSeed(), access);
        if (!replaced.equals(current) || terrablender && CoOConfig.elysiumapiResolveBiomeOnce) {
            cir.setReturnValue(replaced);
        }
    }
}
