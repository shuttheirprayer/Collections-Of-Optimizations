package com.misanthropy.collections_of_optimizations.mixin.neobackports;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Pseudo
@Mixin(targets = "net.nikdo53.neobackports.io.components.DataDefault", remap = false)
public abstract class MixinNeoBackportsDataDefaults {

    @Unique
    private static volatile Map coo$defaultsSource;

    @Unique
    private static volatile ImmutableMap coo$defaultsView;

    @WrapOperation(
            method = "getDefaults",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"
            ),
            require = 0
    )
    private static ImmutableMap coo$reuseDefaultsView(Map source, Operation<ImmutableMap> original) {
        if (!CoOConfig.neobackportsCacheComponentDefaults || source == null) {
            return original.call(source);
        }
        ImmutableMap view = coo$defaultsView;
        if (view != null && coo$defaultsSource == source && view.size() == source.size()) {
            return view;
        }
        ImmutableMap built = original.call(source);
        coo$defaultsView = built;
        coo$defaultsSource = source;
        return built;
    }
}
