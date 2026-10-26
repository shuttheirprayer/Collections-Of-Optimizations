package com.misanthropy.collections_of_optimizations.mixin.cbc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

@Mixin(value = Entity.class, priority = 2000)
public abstract class MixinCbcAutocannonSeat {

    @Shadow public abstract Entity getVehicle();
    @Shadow public abstract float getBbWidth();
    @Shadow public abstract double getY();
    @Shadow public abstract float getEyeHeight();
    @Shadow public abstract Vec3 position();

    @Unique
    private boolean coo$onAutocannon() {
        return this.getVehicle() instanceof PitchOrientedContraptionEntity mount
                && mount.getSeatPos((Entity) (Object) this) != null;
    }

    @ModifyReturnValue(method = "makeBoundingBox", at = @At("RETURN"))
    private AABB coo$seatBoundingBox(AABB original) {
        if (!this.coo$onAutocannon()) {
            return original;
        }
        float half = this.getBbWidth() / 2.0f;
        return new AABB(-half, -half, -half, half, half, half).move(this.position());
    }

    @ModifyReturnValue(method = "getEyeHeight()F", at = @At("RETURN"))
    private float coo$seatEyeHeight(float original) {
        return this.coo$onAutocannon() ? this.getBbWidth() * 0.425f : original;
    }

    @ModifyReturnValue(method = "getEyeY", at = @At("RETURN"))
    private double coo$seatEyeY(double original) {
        return this.coo$onAutocannon() ? this.getY() + (double) this.getBbWidth() * 0.425 : original;
    }

    @ModifyReturnValue(method = "getY(D)D", at = @At("RETURN"))
    private double coo$seatY(double original, double scale) {
        return this.coo$onAutocannon() ? this.getY() + (double) this.getBbWidth() * (scale - 1.0) : original;
    }

    @ModifyReturnValue(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
    private Vec3 coo$seatEyePosition(Vec3 original) {
        if (!this.coo$onAutocannon() || !((Object) this instanceof Player)) {
            return original;
        }
        PitchOrientedContraptionEntity mount = (PitchOrientedContraptionEntity) this.getVehicle();
        Vec3 offset = new Vec3(Direction.UP.step()).scale(0.35 - (double) this.getEyeHeight());
        return original.add(mount.applyRotation(offset, 0.0f));
    }
}
