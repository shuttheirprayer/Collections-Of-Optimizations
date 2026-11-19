package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "punchy.config.PunchyConfig", remap = false)
public abstract class MixinPunchyBlacklistCache {

    @Shadow
    private static punchy.config.PunchyConfig.Data DATA;

    @Unique
    private static final Object[] coo$keys = new Object[8];

    @Unique
    private static final String[] coo$results = new String[2];

    @Unique
    private static int coo$next;

    @Inject(method = "findBlacklistMatch", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$cachedMatch(ItemStack stack, CallbackInfoReturnable<String> cir) {
        if (!CoOConfig.punchyCacheBlacklistMatch || stack == null || DATA == null) {
            return;
        }
        Object list = DATA.itemBlacklist;
        Object tag = stack.getTag();
        for (int slot = 0; slot < 2; slot++) {
            int o = slot * 4;
            if (coo$keys[o] == stack && coo$keys[o + 1] == tag && coo$keys[o + 2] == DATA && coo$keys[o + 3] == list) {
                cir.setReturnValue(coo$results[slot]);
                return;
            }
        }
    }

    @Inject(method = "findBlacklistMatch", at = @At("RETURN"), require = 0)
    private static void coo$rememberMatch(ItemStack stack, CallbackInfoReturnable<String> cir) {
        if (!CoOConfig.punchyCacheBlacklistMatch || stack == null || DATA == null) {
            return;
        }
        int slot = coo$next;
        coo$next = slot ^ 1;
        int o = slot * 4;
        coo$keys[o] = stack;
        coo$keys[o + 1] = stack.getTag();
        coo$keys[o + 2] = DATA;
        coo$keys[o + 3] = DATA.itemBlacklist;
        coo$results[slot] = cir.getReturnValue();
    }
}
