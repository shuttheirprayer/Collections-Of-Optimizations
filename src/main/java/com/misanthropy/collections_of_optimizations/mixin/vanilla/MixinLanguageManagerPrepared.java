package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.ReloadPrefetch;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(LanguageManager.class)
public abstract class MixinLanguageManagerPrepared {

    @WrapOperation(
            method = "onResourceManagerReload",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/language/ClientLanguage;loadFrom(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resources/language/ClientLanguage;"),
            require = 0
    )
    private ClientLanguage coo$usePrepared(ResourceManager manager, List<String> codes, boolean rightToLeft, Operation<ClientLanguage> original) {
        ClientLanguage prepared = ReloadPrefetch.language(manager, codes, rightToLeft);
        return prepared != null ? prepared : original.call(manager, codes, rightToLeft);
    }
}
