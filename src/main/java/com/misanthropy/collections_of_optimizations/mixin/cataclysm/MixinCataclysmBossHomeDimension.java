package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = {
        "com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.LLibrary_Boss_Monster",
        "com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster"
}, remap = false)
public abstract class MixinCataclysmBossHomeDimension {

    @Unique
    private String coo$homeDimString;
    @Unique
    private ResourceLocation coo$homeDimLocation;
    @Unique
    private ResourceKey<?> coo$homeKeyRegistry;
    @Unique
    private ResourceLocation coo$homeKeyLocation;
    @Unique
    private ResourceKey<?> coo$homeKey;

    @WrapOperation(
            method = "ReturnToHome",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;m_135820_(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            require = 0
    )
    private ResourceLocation coo$cachedParse(String dimStr, Operation<ResourceLocation> original) {
        if (!CoOConfig.cataclysmCacheHomeDimension) {
            return original.call(dimStr);
        }
        if (dimStr != null && dimStr == this.coo$homeDimString) {
            return this.coo$homeDimLocation;
        }
        ResourceLocation parsed = original.call(dimStr);
        this.coo$homeDimString = dimStr;
        this.coo$homeDimLocation = parsed;
        return parsed;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @WrapOperation(
            method = "ReturnToHome",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceKey;m_135785_(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceKey;"),
            require = 0
    )
    private ResourceKey coo$cachedKey(ResourceKey registry, ResourceLocation location, Operation<ResourceKey> original) {
        if (!CoOConfig.cataclysmCacheHomeDimension) {
            return original.call(registry, location);
        }
        if (location != null && location == this.coo$homeKeyLocation && registry == this.coo$homeKeyRegistry) {
            return this.coo$homeKey;
        }
        ResourceKey key = original.call(registry, location);
        this.coo$homeKeyRegistry = registry;
        this.coo$homeKeyLocation = location;
        this.coo$homeKey = key;
        return key;
    }
}
