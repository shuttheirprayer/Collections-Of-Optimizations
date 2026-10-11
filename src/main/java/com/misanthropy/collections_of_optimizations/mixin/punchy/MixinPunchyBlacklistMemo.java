package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PunchyBlacklistMemo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import punchy.compat.TaczBlacklistCompat;
import punchy.config.PunchyConfig;

@Mixin(value = PunchyConfig.class, remap = false)
public abstract class MixinPunchyBlacklistMemo {

    @Shadow
    private static PunchyConfig.Data DATA;

    @WrapMethod(method = "findBlacklistMatch", require = 0)
    private static String coo$memoBlacklistMatch(ItemStack stack, Operation<String> original) {
        PunchyConfig.Data data = DATA;
        if (!CoOConfig.punchyMemoBlacklistMatch || data == null || data.itemBlacklist == null
                || stack == null || stack.isEmpty() || TaczBlacklistCompat.isAvailable()) {
            return original.call(stack);
        }
        PunchyBlacklistMemo memo = PunchyBlacklistMemo.forSource(data.itemBlacklist);
        Item item = stack.getItem();
        Object known = memo.get(item);
        if (known != null) {
            return known == PunchyBlacklistMemo.NO_MATCH ? null : (String) known;
        }
        String match = original.call(stack);
        memo.put(item, match);
        return match;
    }
}
