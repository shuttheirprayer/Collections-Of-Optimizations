package com.misanthropy.collections_of_optimizations.mixin.arselemental;

import alexthw.ars_elemental.util.CompatUtils;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = CompatUtils.class, remap = false)
public class MixinAECompatUtils {

    @Inject(method = "getCurio", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanGetCurio(LivingEntity player, Predicate<ItemStack> predicate, CallbackInfoReturnable<SlotResult> cir) {
        if (!CoOConfig.arselementalLeanCurioFallback) {
            return;
        }
        var lazy = CuriosApi.getCuriosInventory(player);
        if (lazy.isPresent()) {
            var optional = lazy.resolve();
            if (optional.isPresent()) {
                SlotResult found = optional.get().findFirstCurio(predicate).orElse(null);
                if (found != null) {
                    cir.setReturnValue(found);
                    return;
                }
            }
        }
        cir.setReturnValue(new SlotResult(null, ItemStack.EMPTY));
    }

    @Inject(method = "getCurios", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanGetCurios(LivingEntity player, Predicate<ItemStack> predicate, CallbackInfoReturnable<List<SlotResult>> cir) {
        if (!CoOConfig.arselementalLeanCurioFallback) {
            return;
        }
        var lazy = CuriosApi.getCuriosInventory(player);
        if (lazy.isPresent()) {
            var optional = lazy.resolve();
            if (optional.isPresent()) {
                cir.setReturnValue(optional.get().findCurios(predicate));
                return;
            }
        }
        cir.setReturnValue(NonNullList.withSize(2, new SlotResult(null, ItemStack.EMPTY)));
    }
}
