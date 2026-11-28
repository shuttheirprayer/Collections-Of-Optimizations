package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IdleEntities;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ExperienceOrb.class, priority = 3000)
public abstract class MixinExperienceOrbIdle implements IdleEntities.State {

    @Shadow
    private int age;

    @Shadow
    private Player followingPlayer;

    @Override
    public boolean coo$skipTick() {
        ExperienceOrb self = (ExperienceOrb) (Object) this;
        if (!IdleEntities.asleep(self)) {
            if ((self.tickCount & 3) != 2 || !CoOConfig.vanillaSleepIdleOrbs || this.followingPlayer != null || self.getClass() != ExperienceOrb.class) {
                return false;
            }
            IdleEntities.trySleep(self, true, true);
            if (!IdleEntities.asleep(self)) {
                return false;
            }
        }
        if (CoOConfig.vanillaSleepIdleOrbs && this.coo$keepSleeping(self)) {
            return true;
        }
        this.coo$wake();
        return false;
    }

    private boolean coo$keepSleeping(ExperienceOrb self) {
        if (self.tickCount % 20 == 1 || !IdleEntities.stillIdle(self)) {
            return false;
        }
        if (((self.tickCount + self.getId()) & 3) == 0 && !IdleEntities.settled(self, true, true)) {
            return false;
        }
        if (this.age + 1 >= 6000) {
            return false;
        }
        this.age++;
        return true;
    }
}
