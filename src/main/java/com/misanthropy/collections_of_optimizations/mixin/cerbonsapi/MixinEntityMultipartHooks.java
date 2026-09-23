package com.misanthropy.collections_of_optimizations.mixin.cerbonsapi;

import com.cerbon.cerbons_api.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.cerbons_api.api.multipart_entities.entity.MultipartEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntityMultipartHooks {

    @Unique
    private static final byte COO$KNOWN = 1;

    @Unique
    private static final byte COO$MULTIPART = 2;

    @Unique
    private static final byte COO$AWARE = 4;

    @Unique
    private byte coo$multipartKind;

    @Unique
    private byte coo$multipartKind() {
        byte kind = this.coo$multipartKind;
        if (kind == 0) {
            kind = COO$KNOWN;
            if (this instanceof MultipartEntity) {
                kind |= COO$MULTIPART;
            }
            if (this instanceof MultipartAwareEntity) {
                kind |= COO$AWARE;
            }
            this.coo$multipartKind = kind;
        }
        return kind;
    }

    @ModifyReturnValue(method = "getBoundingBox", at = @At("RETURN"))
    private AABB coo$compoundBoundingBox(AABB original) {
        return (this.coo$multipartKind() & COO$MULTIPART) != 0 ? ((MultipartEntity) this).getCompoundBoundingBox(original) : original;
    }

    @Inject(method = "setPosRaw", at = @At("TAIL"))
    private void coo$notifyMultipartPos(double x, double y, double z, CallbackInfo ci) {
        if ((this.coo$multipartKind() & COO$AWARE) != 0) {
            ((MultipartAwareEntity) this).onSetPos(x, y, z);
        }
    }
}
