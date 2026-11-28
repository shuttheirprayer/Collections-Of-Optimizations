package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IdleEntities;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AbstractArrow.class, priority = 3000)
public abstract class MixinAbstractArrowIdle implements IdleEntities.State {

    @Shadow
    protected boolean inGround;

    @Shadow
    protected int inGroundTime;

    @Shadow
    private int life;

    @Shadow
    private BlockState lastState;

    @Shadow
    public int shakeTime;

    @Shadow
    public abstract boolean isNoPhysics();

    @Override
    public boolean coo$skipTick() {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!IdleEntities.asleep(self) && !this.coo$settle(self)) {
            return false;
        }
        if (CoOConfig.vanillaSleepIdleArrows && this.coo$keepSleeping(self)) {
            return true;
        }
        this.coo$wake();
        return false;
    }

    private boolean coo$settle(AbstractArrow self) {
        if ((self.tickCount & 7) != 0 || !CoOConfig.vanillaSleepIdleArrows || !this.inGround || this.inGroundTime < 8 || this.shakeTime > 0) {
            return false;
        }
        Class<?> type = self.getClass();
        if ((type != Arrow.class && type != SpectralArrow.class) || self.level().isClientSide || self.isRemoved()
                || self.isOnFire() || self.isNoPhysics() || self.isVehicle() || self.isPassenger()) {
            return false;
        }
        BlockState feet = self.level().getBlockState(self.blockPosition());
        if (feet != this.lastState || feet.isAir()) {
            return false;
        }
        this.coo$sleep(self.position(), self.getDeltaMovement(), feet, feet);
        return true;
    }

    private boolean coo$keepSleeping(AbstractArrow self) {
        if (!IdleEntities.stillIdle(self) || this.life + 1 >= 1200) {
            return false;
        }
        if (((self.tickCount + self.getId()) & 3) == 0 && self.isOnFire()) {
            return false;
        }
        this.life++;
        this.inGroundTime++;
        return true;
    }
}
