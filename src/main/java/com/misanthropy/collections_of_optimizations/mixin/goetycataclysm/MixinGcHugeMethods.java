package com.misanthropy.collections_of_optimizations.mixin.goetycataclysm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = {
        "com.Polarice3.goety_cataclysm.client.render.model.LeviathanServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.ZombieAmethystCrabModel"
}, remap = false, priority = 10000)
public abstract class MixinGcHugeMethods {
}
