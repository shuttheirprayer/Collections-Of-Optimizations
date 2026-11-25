package com.misanthropy.collections_of_optimizations.mixin.terrablender;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CachedNamespacedRule;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(targets = "terrablender.worldgen.surface.NamespacedSurfaceRuleSource$NamespacedRule", remap = false)
public abstract class MixinNamespacedRule implements CachedNamespacedRule {

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

    @Override
    public BlockState coo$apply(int x, int y, int z) {
        SurfaceRules.SurfaceRule rule = this.coo$ruleFor(((SurfaceContextAccessor) (Object) this.context).coo$getBiome().get());
        BlockState state = rule == null ? null : rule.tryApply(x, y, z);
        if (state == null) {
            state = this.baseRule.tryApply(x, y, z);
        }
        return state;
    }

    @Unique
    private SurfaceRules.SurfaceRule coo$ruleFor(Holder<Biome> biome) {
        if (biome != this.coo$cachedBiome) {
            this.coo$cachedBiome = biome;
            ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
            this.coo$cachedRule = key == null ? null : this.rules.get(key.location().getNamespace());
        }
        return this.coo$cachedRule;
    }

    @Redirect(method = {"tryApply", "m_183550_"}, require = 0, expect = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Ljava/util/function/Predicate;)Z", remap = true))
    private boolean coo$hasRule(Holder<Biome> biome, Predicate<ResourceKey<Biome>> predicate) {
        return CoOConfig.terrablenderCacheNamespaceRule ? this.coo$ruleFor(biome) != null : biome.is(predicate);
    }

    @Redirect(method = {"tryApply", "m_183550_"}, require = 0, expect = 0,
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object coo$cachedRule(Map<?, ?> rules, Object namespace) {
        return CoOConfig.terrablenderCacheNamespaceRule ? this.coo$cachedRule : rules.get(namespace);
    }
}
