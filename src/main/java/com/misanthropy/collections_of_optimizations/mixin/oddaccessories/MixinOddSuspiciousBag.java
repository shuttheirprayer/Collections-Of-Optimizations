package com.misanthropy.collections_of_optimizations.mixin.oddaccessories;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.OddAccessoriesRewardPool;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.oddaccessories.configuration.ConfigConfiguration;
import net.oddaccessories.item.SuspiciousBagItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(value = SuspiciousBagItem.class, remap = false)
public abstract class MixinOddSuspiciousBag {

    @Shadow
    @Final
    private static Set<String> EXCLUDED;

    @Inject(method = "rollReward", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$memoRewardPool(RandomSource random, CallbackInfoReturnable<Item> cir) {
        if (!CoOConfig.oddaccessoriesMemoBagRewardPool) {
            return;
        }

        List<Item> pool = OddAccessoriesRewardPool.pool(EXCLUDED);
        if (pool == null) {
            return;
        }
        if (pool.isEmpty()) {
            cir.setReturnValue(null);
            return;
        }

        Item reward = pool.get(random.nextInt(pool.size()));
        for (int tries = 0; tries < 8; tries++) {
            int keep = ConfigConfiguration.dropRarity(reward);
            if (keep <= 1 || random.nextInt(keep) == 0) {
                break;
            }
            reward = pool.get(random.nextInt(pool.size()));
        }
        cir.setReturnValue(reward);
    }
}
