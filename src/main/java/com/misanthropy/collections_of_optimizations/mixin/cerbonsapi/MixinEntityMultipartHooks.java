package com.misanthropy.collections_of_optimizations.mixin.cerbonsapi;

import com.cerbon.cerbons_api.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.cerbons_api.api.multipart_entities.entity.MultipartEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntityMultipartHooks {

    @ModifyReturnValue(method = "getBoundingBox", at = @At("RETURN"))
    private AABB coo$compoundBoundingBox(AABB original) {
        return this instanceof MultipartEntity multipart ? multipart.getCompoundBoundingBox(original) : original;
    }

    @Inject(method = "setPosRaw", at = @At("TAIL"))
    private void coo$notifyMultipartPos(double x, double y, double z, CallbackInfo ci) {
        if (this instanceof MultipartAwareEntity aware) {
            aware.onSetPos(x, y, z);
        }
    }
}
