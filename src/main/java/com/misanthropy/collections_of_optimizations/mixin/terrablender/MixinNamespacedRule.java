package com.misanthropy.collections_of_optimizations.mixin.terrablender;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(targets = "terrablender.worldgen.surface.NamespacedSurfaceRuleSource$NamespacedRule", remap = false)
public abstract class MixinNamespacedRule {

    @Shadow
    @Final
    private SurfaceRules.Context context;

    @Shadow
    @Final
    private SurfaceRules.SurfaceRule baseRule;

    @Shadow
    @Final
    private Map<String, SurfaceRules.SurfaceRule> rules;

    @Unique
    private Holder<Biome> coo$cachedBiome;

    @Unique
    private SurfaceRules.SurfaceRule coo$cachedRule;

    @WrapMethod(method = "tryApply", remap = true, require = 0)
    private BlockState coo$cacheNamespaceLookup(int x, int y, int z, Operation<BlockState> original) {
        if (!CoOConfig.terrablenderCacheNamespaceRule) {
            return original.call(x, y, z);
        }

        Holder<Biome> biome = ((SurfaceContextAccessor) (Object) this.context).coo$getBiome().get();
        if (biome != this.coo$cachedBiome) {
            this.coo$cachedBiome = biome;
            ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
            this.coo$cachedRule = key == null ? null : this.rules.get(key.location().getNamespace());
        }

        BlockState state = this.coo$cachedRule == null ? null : this.coo$cachedRule.tryApply(x, y, z);
        if (state == null) {
            state = this.baseRule.tryApply(x, y, z);
        }
        return state;
    }
}
