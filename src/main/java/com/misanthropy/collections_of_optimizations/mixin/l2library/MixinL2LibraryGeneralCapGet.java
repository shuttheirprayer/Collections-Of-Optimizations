package com.misanthropy.collections_of_optimizations.mixin.l2library;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.xkmc.l2library.capability.entity.GeneralCapabilityHolder;
import dev.xkmc.l2library.capability.entity.GeneralCapabilityTemplate;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GeneralCapabilityHolder.class, remap = false)
public abstract class MixinL2LibraryGeneralCapGet {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(
            method = "get(Lnet/minecraftforge/common/capabilities/ICapabilityProvider;)Ldev/xkmc/l2library/capability/entity/GeneralCapabilityTemplate;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$resolveCapabilityOnce(ICapabilityProvider provider, CallbackInfoReturnable<GeneralCapabilityTemplate> cir) {
        if (!CoOConfig.l2libraryLeanCapabilityResolve || provider == null) {
            return;
        }
        GeneralCapabilityHolder<?, ?> self = (GeneralCapabilityHolder<?, ?>) (Object) this;
        Object data = provider.getCapability(self.capability).orElse(null);
        if (data instanceof GeneralCapabilityTemplate<?, ?> template) {
            cir.setReturnValue(template.check());
        }
    }
}
