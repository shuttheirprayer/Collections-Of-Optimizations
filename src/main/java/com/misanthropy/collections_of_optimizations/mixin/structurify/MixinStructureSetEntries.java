package com.misanthropy.collections_of_optimizations.mixin.structurify;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.StructurifyConfigMirror;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = StructureSet.class, priority = 1500)
public abstract class MixinStructureSetEntries {

    @Unique
    private transient StructurifyConfigMirror.Cached<List<StructureSet.StructureSelectionEntry>> coo$entries;

    @Inject(
            method = "structures",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$cachedEntries(CallbackInfoReturnable<List<StructureSet.StructureSelectionEntry>> cir) {
        if (!CoOConfig.structurifyCacheStructureSetEntries) {
            return;
        }
        StructurifyConfigMirror.Cached<List<StructureSet.StructureSelectionEntry>> cached = this.coo$entries;
        if (cached != null && cached.isCurrent()) {
            cir.setReturnValue(cached.value());
        }
    }

    @Inject(
            method = "structures",
            at = @At("RETURN"),
            require = 0
    )
    private void coo$storeEntries(CallbackInfoReturnable<List<StructureSet.StructureSelectionEntry>> cir) {
        if (!CoOConfig.structurifyCacheStructureSetEntries) {
            return;
        }
        this.coo$entries = new StructurifyConfigMirror.Cached<>(
                StructurifyConfigMirror.generation(), cir.getReturnValue());
    }
}
