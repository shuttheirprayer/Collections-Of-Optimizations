package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "xaero.map.gui.GuiMap", remap = false, priority = 10000)
public abstract class MixinXaeroMapHugeMethods {
}
