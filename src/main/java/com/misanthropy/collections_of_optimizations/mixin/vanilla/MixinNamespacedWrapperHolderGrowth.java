package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraftforge.registries.NamespacedWrapper", remap = false)
public abstract class MixinNamespacedWrapperHolderGrowth<T> {
    @Shadow
    private ObjectList<Holder.Reference<T>> holdersById;

    @Inject(method = "onAdded", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectList;size(I)V"))
    private void coo$growGeometrically(RegistryManager stage, int id, ResourceKey<T> key, T newValue, T oldValue,
                                       CallbackInfoReturnable<Holder.Reference<T>> cir) {
        if (CoOConfig.vanillaGeometricHolderGrowth && holdersById instanceof ObjectArrayList<Holder.Reference<T>> list) {
            list.ensureCapacity(id + 1);
        }
    }
}
