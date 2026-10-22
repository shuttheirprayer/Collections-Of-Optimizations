package com.misanthropy.collections_of_optimizations.mixin.kubejs;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.GeneratedPackIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Pseudo
@Mixin(targets = "dev.latvian.mods.kubejs.script.data.GeneratedResourcePack", remap = false)
public abstract class MixinGeneratedResourcePackIndex {

    @Shadow
    @Final
    private PackType packType;

    @Unique
    private volatile GeneratedPackIndex coo$index;

    @Shadow
    public abstract Map<ResourceLocation, ?> getGenerated();

    @Inject(method = "m_8031_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$listFromIndex(PackType type, String namespace, String path, PackResources.ResourceOutput output, CallbackInfo ci) {
        if (!CoOConfig.kubejsIndexGeneratedPack) {
            return;
        }
        ci.cancel();
        if (type != packType) {
            return;
        }
        Map<ResourceLocation, ?> generated = getGenerated();
        GeneratedPackIndex index = coo$index;
        if (index == null || !index.covers(generated)) {
            index = GeneratedPackIndex.build(generated);
            coo$index = index;
        }
        index.list(namespace, path.endsWith("/") ? path : path + "/", output);
    }
}
