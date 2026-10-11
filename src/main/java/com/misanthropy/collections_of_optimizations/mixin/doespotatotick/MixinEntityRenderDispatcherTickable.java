package com.misanthropy.collections_of_optimizations.mixin.doespotatotick;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.kall.doespotatotick.config.ConfigConstants;
import me.kall.doespotatotick.ext.Tickable;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcherTickable {

    @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
    private boolean coo$skipUntickable(boolean original, @Local(argsOnly = true) Entity entity) {
        if (!original || !ConfigConstants.skipRenderingUntickable) {
            return original;
        }
        return ((Tickable) entity).dpt$tickable();
    }
}
