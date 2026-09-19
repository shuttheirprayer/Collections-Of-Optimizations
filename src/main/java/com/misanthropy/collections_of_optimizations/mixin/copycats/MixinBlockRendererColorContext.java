package com.misanthropy.collections_of_optimizations.mixin.copycats;

import com.copycatsplus.copycats.foundation.copycat.CopycatExternalContext;
import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateTextureAtlasSprite;
import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockRenderer.class, remap = false)
public abstract class MixinBlockRendererColorContext {

    @Redirect(
            method = "getVertexColors",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/model/color/ColorProvider;getColors(Lme/jellysquid/mods/sodium/client/world/WorldSlice;Lnet/minecraft/core/BlockPos;Ljava/lang/Object;Lme/jellysquid/mods/sodium/client/model/quad/ModelQuadView;[I)V"
            ),
            require = 1
    )
    private void coo$colorWithCopycatProperty(ColorProvider<Object> provider, WorldSlice view, BlockPos pos,
                                              Object state, ModelQuadView quad, int[] output) {
        TextureAtlasSprite sprite = quad.getSprite();

        if (sprite instanceof MultiStateTextureAtlasSprite) {
            CopycatExternalContext.setPropertyForBlockColor(((MultiStateTextureAtlasSprite) sprite).getProperty());
        }

        provider.getColors(view, pos, state, quad, output);

        CopycatExternalContext.setPropertyForBlockColor(null);
    }
}
