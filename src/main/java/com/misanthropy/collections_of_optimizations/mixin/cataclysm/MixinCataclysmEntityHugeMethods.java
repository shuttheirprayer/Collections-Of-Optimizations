package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = {
        "com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Entity",
        "com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity"
}, remap = false, priority = 10000)
public abstract class MixinCataclysmEntityHugeMethods {
}
