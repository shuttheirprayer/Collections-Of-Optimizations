package com.misanthropy.collections_of_optimizations.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FdToolActionPass;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ToolAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.crafting.ingredient.ToolActionIngredient;

import java.util.stream.Stream;

@Mixin(value = ToolActionIngredient.class, remap = false)
public abstract class MixinFdToolActionIngredient {

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/Ingredient;<init>(Ljava/util/stream/Stream;)V"
            ),
            require = 0
    )
    private static Stream<? extends Ingredient.Value> coo$shareToolActionScan(Stream<? extends Ingredient.Value> values,
                                                                              @Local(argsOnly = true) ToolAction action) {
        if (!CoOConfig.farmersdelightShareToolActionScan) {
            return values;
        }
        return FdToolActionPass.values(action, values);
    }
}
