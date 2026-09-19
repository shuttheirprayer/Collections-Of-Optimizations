package com.misanthropy.collections_of_optimizations.mixin.iceandfire;

import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = RenderDragonBase.class, remap = false)
public abstract class MixinRenderDragonBase {

    @Unique
    private Map<String, ResourceLocation[]> coo$dragonTextures;

    @Unique
    private static int coo$packDragonState(EntityDragonBase dragon) {
        int stage = dragon.getDragonStage();
        if (stage < 0 || stage > 7) {
            return -1;
        }
        return stage << 5
                | (dragon.isModelDead() ? 16 : 0)
                | (dragon.isMale() ? 8 : 0)
                | (dragon.isSkeletal() ? 4 : 0)
                | (dragon.isSleeping() ? 2 : 0)
                | (dragon.isBlinking() ? 1 : 0);
    }

    @WrapMethod(method = "getTextureLocation", require = 0)
    private ResourceLocation coo$memoDragonTexture(EntityDragonBase dragon, Operation<ResourceLocation> original) {
        if (!CoOConfig.iceandfireCacheDragonTexture) {
            return original.call(dragon);
        }

        int index = coo$packDragonState(dragon);
        if (index < 0) {
            return original.call(dragon);
        }

        Map<String, ResourceLocation[]> textures = this.coo$dragonTextures;
        String variant = dragon.getVariantName(dragon.getVariant());
        if (textures != null) {
            ResourceLocation[] slots = textures.get(variant);
            if (slots != null && slots[index] != null) {
                return slots[index];
            }
        }

        ResourceLocation resolved = original.call(dragon);
        if (resolved == null) {
            return null;
        }
        if (textures == null) {
            textures = new HashMap<>();
            this.coo$dragonTextures = textures;
        }
        textures.computeIfAbsent(variant, key -> new ResourceLocation[256])[index] = resolved;
        return resolved;
    }
}
