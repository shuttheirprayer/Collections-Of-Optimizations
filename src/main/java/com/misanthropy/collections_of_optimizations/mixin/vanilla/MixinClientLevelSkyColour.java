package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevelSkyColour {

    @Unique
    private Vec3 coo$skyColour;

    @Unique
    private double coo$skyX;

    @Unique
    private double coo$skyY;

    @Unique
    private double coo$skyZ;

    @Unique
    private float coo$skyPartial;

    @Unique
    private int coo$skyStamp = Integer.MIN_VALUE;

    @WrapMethod(method = "getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;", require = 0)
    private Vec3 coo$memoSkyColour(Vec3 pos, float partialTick, Operation<Vec3> original) {
        if (!CoOConfig.vanillaMemoSkyColour) {
            return original.call(pos, partialTick);
        }

        int stamp = ClientTickStamp.currentOnRenderThread();
        if (stamp == -1) {
            return original.call(pos, partialTick);
        }

        Vec3 cached = this.coo$skyColour;
        if (cached != null
                && stamp == this.coo$skyStamp
                && (CoOConfig.vanillaMemoSkyColourPerTick
                        || (partialTick == this.coo$skyPartial
                                && pos.x == this.coo$skyX
                                && pos.y == this.coo$skyY
                                && pos.z == this.coo$skyZ))) {
            return cached;
        }

        Vec3 value = original.call(pos, partialTick);
        this.coo$skyColour = value;
        this.coo$skyStamp = stamp;
        this.coo$skyPartial = partialTick;
        this.coo$skyX = pos.x;
        this.coo$skyY = pos.y;
        this.coo$skyZ = pos.z;
        return value;
    }
}
