package com.misanthropy.collections_of_optimizations.mixin.cookingforblockheads;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.RecipeCacheGeneration;
import net.blay09.mods.cookingforblockheads.block.BlockKitchen;
import net.blay09.mods.cookingforblockheads.tile.OvenBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OvenBlockEntity.class, remap = false)
public abstract class MixinOvenBlockEntity {

    @Unique
    private static final int COO$SMELT_CACHE_SIZE = 16;

    @Unique
    private ItemStack[] coo$smeltInputs;

    @Unique
    private ItemStack[] coo$smeltResults;

    @Unique
    private int coo$smeltNext;

    @Unique
    private RecipeManager coo$smeltManager;

    @Unique
    private int coo$smeltGeneration;

    @WrapMethod(method = "getSmeltingResult", require = 0)
    private ItemStack coo$cachedSmeltingResult(ItemStack input, Operation<ItemStack> original) {
        if (!CoOConfig.cookingforblockheadsCacheOvenSmeltingResult || input.isEmpty()) {
            return original.call(input);
        }
        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null) {
            return original.call(input);
        }
        RecipeManager manager = level.getRecipeManager();
        int generation = RecipeCacheGeneration.generation();
        ItemStack[] inputs = this.coo$smeltInputs;
        if (inputs == null || manager != this.coo$smeltManager || generation != this.coo$smeltGeneration) {
            inputs = new ItemStack[COO$SMELT_CACHE_SIZE];
            this.coo$smeltInputs = inputs;
            this.coo$smeltResults = new ItemStack[COO$SMELT_CACHE_SIZE];
            this.coo$smeltNext = 0;
            this.coo$smeltManager = manager;
            this.coo$smeltGeneration = generation;
        }
        for (int i = 0; i < inputs.length; i++) {
            ItemStack key = inputs[i];
            if (key == null) {
                break;
            }
            if (ItemStack.isSameItemSameTags(key, input)) {
                return this.coo$smeltResults[i];
            }
        }
        ItemStack result = original.call(input);
        int slot = this.coo$smeltNext;
        inputs[slot] = input.copyWithCount(1);
        this.coo$smeltResults[slot] = result;
        this.coo$smeltNext = (slot + 1) % COO$SMELT_CACHE_SIZE;
        return result;
    }

    @Inject(method = "getFacing", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$facingFromState(CallbackInfoReturnable<Direction> cir) {
        if (!CoOConfig.cookingforblockheadsOvenFacingFromState) {
            return;
        }
        BlockState state = ((BlockEntity) (Object) this).getBlockState();
        if (state.hasProperty(BlockKitchen.FACING)) {
            cir.setReturnValue(state.getValue(BlockKitchen.FACING));
        }
    }
}
