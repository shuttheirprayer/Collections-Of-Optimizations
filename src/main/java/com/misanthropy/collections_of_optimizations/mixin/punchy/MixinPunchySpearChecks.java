package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PunchyMemos;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import punchy.client.render.ToolKindResolver;

@Mixin(value = ToolKindResolver.class, remap = false)
public abstract class MixinPunchySpearChecks {

    @WrapMethod(method = "isSpearClass", require = 0)
    private static boolean coo$memoSpearClass(Item item, Operation<Boolean> original) {
        if (!CoOConfig.punchyMemoSpearChecks || item == null) {
            return original.call(item);
        }
        Class<?> type = item.getClass();
        Boolean known = PunchyMemos.spearClass(type);
        if (known != null) {
            return known;
        }
        boolean spear = original.call(item);
        PunchyMemos.rememberSpearClass(type, spear);
        return spear;
    }

    @WrapMethod(method = "isSpearId", require = 0)
    private static boolean coo$memoSpearId(Item item, Operation<Boolean> original) {
        if (!CoOConfig.punchyMemoSpearChecks || item == null) {
            return original.call(item);
        }
        Boolean known = PunchyMemos.spearId(item);
        if (known != null) {
            return known;
        }
        boolean spear = original.call(item);
        PunchyMemos.rememberSpearId(item, spear);
        return spear;
    }
}
