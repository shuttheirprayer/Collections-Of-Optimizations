package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.RegistrySnapshotReuse;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = ForgeHooks.class, remap = false)
public abstract class MixinForgeHooksSnapshotSave {

    @WrapOperation(
            method = "writeAdditionalLevelSaveData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/registries/RegistryManager;takeSnapshot(Z)Ljava/util/Map;"
            ),
            require = 0
    )
    private static Map<?, ?> coo$markSavingSnapshot(RegistryManager manager, boolean savingToDisc, Operation<Map<?, ?>> original) {
        RegistrySnapshotReuse.beginSave();
        try {
            return original.call(manager, savingToDisc);
        } finally {
            RegistrySnapshotReuse.endSave();
        }
    }
}
