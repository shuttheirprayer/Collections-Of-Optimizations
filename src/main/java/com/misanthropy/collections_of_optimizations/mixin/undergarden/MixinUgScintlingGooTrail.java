package com.misanthropy.collections_of_optimizations.mixin.undergarden;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "quek.undergarden.entity.animal.Scintling", remap = false)
public abstract class MixinUgScintlingGooTrail {

    @Unique
    private int coo$nextIdleGooTick;

    @WrapOperation(
            method = "m_8107_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/event/ForgeEventFactory;getMobGriefingEvent(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;)Z"
            ),
            remap = false,
            require = 0
    )
    private boolean coo$leanGooTrail(Level level, Entity entity, Operation<Boolean> original) {
        if (!CoOConfig.undergardenLeanScintlingGooTrail) {
            return original.call(level, entity);
        }
        if (level == null || level.isClientSide()) {
            return false;
        }
        Entity self = (Entity) (Object) this;
        int interval = CoOConfig.undergardenScintlingIdleGooInterval;
        if (interval > 1 && self.getX() == self.xo && self.getY() == self.yo && self.getZ() == self.zo) {
            if (self.tickCount < this.coo$nextIdleGooTick) {
                return false;
            }
            this.coo$nextIdleGooTick = self.tickCount + interval;
        } else {
            this.coo$nextIdleGooTick = 0;
        }
        return original.call(level, entity);
    }
}
