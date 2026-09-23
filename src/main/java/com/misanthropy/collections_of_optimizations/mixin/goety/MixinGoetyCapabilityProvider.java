package com.misanthropy.collections_of_optimizations.mixin.goety;

import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.capabilities.misc.MiscProvider;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEProvider;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = {MiscProvider.class, SEProvider.class, LichProvider.class}, remap = false)
public abstract class MixinGoetyCapabilityProvider {

    @Unique
    private Capability<?> coo$cachedCapability;

    @Unique
    private LazyOptional<?> coo$cachedOptional;

    @SuppressWarnings("unchecked")
    @WrapMethod(method = "getCapability")
    private <T> LazyOptional<T> coo$reuseCapabilityOptional(Capability<T> capability, Direction side,
                                                            Operation<LazyOptional<T>> original) {
        if (!CoOConfig.goetyCacheCapabilityOptional) {
            return original.call(capability, side);
        }
        LazyOptional<?> cached = this.coo$cachedOptional;
        if (cached != null) {
            return this.coo$cachedCapability == capability ? (LazyOptional<T>) cached : LazyOptional.empty();
        }
        LazyOptional<T> resolved = original.call(capability, side);
        if (cached == null && resolved != null && resolved.isPresent()) {
            this.coo$cachedOptional = resolved;
            this.coo$cachedCapability = capability;
        }
        return resolved;
    }
}
