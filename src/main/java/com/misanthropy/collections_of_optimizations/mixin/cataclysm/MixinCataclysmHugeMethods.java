package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = {
        "com.github.L_Ender.cataclysm.client.model.entity.Ignis_Model",
        "com.github.L_Ender.cataclysm.client.model.entity.The_Leviathan_Model",
        "com.github.L_Ender.cataclysm.client.model.entity.Ancient_Remnant_Model",
        "com.github.L_Ender.cataclysm.client.model.entity.Ender_Guardian_Model",
        "com.github.L_Ender.cataclysm.client.model.entity.Amethyst_Crab_Model"
}, remap = false, priority = 10000)
public abstract class MixinCataclysmHugeMethods {
}
