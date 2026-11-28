package com.misanthropy.collections_of_optimizations.mixin.ftblibrary;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.ReloadPrefetch;
import dev.ftb.mods.ftblibrary.sidebar.SidebarButtonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = SidebarButtonManager.class, remap = false)
public abstract class MixinSidebarPreparedReads {

    @Unique
    private ResourceManager coo$manager;

    @WrapOperation(
            method = "m_6213_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceManager;getResourceStack(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/List;"),
            remap = true,
            require = 0
    )
    private List<Resource> coo$preparedStack(ResourceManager manager, ResourceLocation id, Operation<List<Resource>> original) {
        this.coo$manager = manager;
        List<Resource> prepared = ReloadPrefetch.sidebarStack(manager, id);
        return prepared != null ? prepared : original.call(manager, id);
    }

    @WrapOperation(
            method = "m_6213_",
            at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftblibrary/sidebar/SidebarButtonManager;readJson(Lnet/minecraft/server/packs/resources/Resource;)Lcom/google/gson/JsonElement;"),
            require = 0
    )
    private JsonElement coo$preparedJson(SidebarButtonManager self, Resource resource, Operation<JsonElement> original) {
        JsonElement prepared = this.coo$manager == null ? null : ReloadPrefetch.sidebarJson(this.coo$manager, resource);
        return prepared != null ? prepared : original.call(self, resource);
    }
}
