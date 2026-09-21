package com.misanthropy.collections_of_optimizations.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.Basket;
import vectorwing.farmersdelight.common.block.entity.BasketBlockEntity;

import java.util.List;

@Mixin(value = BasketBlockEntity.class, remap = false)
public abstract class MixinFdBasketBlockEntity {

    @Unique
    private static final AABB[][] COO$COLLECTION_BOXES = new AABB[6][];

    @Inject(method = "pullItems", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanPullItems(Level level, Basket basket, int facingIndex, CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.farmersdelightLeanBasketScan || facingIndex < 0 || facingIndex >= COO$COLLECTION_BOXES.length) {
            return;
        }
        AABB[] boxes = COO$COLLECTION_BOXES[facingIndex];
        if (boxes == null) {
            List<AABB> shapeBoxes = basket.getFacingCollectionArea(facingIndex).toAabbs();
            boxes = shapeBoxes.toArray(new AABB[0]);
            COO$COLLECTION_BOXES[facingIndex] = boxes;
        }
        double offsetX = basket.getLevelX() - 0.5D;
        double offsetY = basket.getLevelY() - 0.5D;
        double offsetZ = basket.getLevelZ() - 0.5D;
        for (AABB box : boxes) {
            List<ItemEntity> found = level.getEntitiesOfClass(ItemEntity.class, box.move(offsetX, offsetY, offsetZ), Entity::isAlive);
            for (int i = 0; i < found.size(); i++) {
                if (BasketBlockEntity.captureItem(basket, found.get(i))) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
        cir.setReturnValue(false);
    }

    @WrapOperation(
            method = "captureItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;m_32045_(Lnet/minecraft/world/item/ItemStack;)V"
            ),
            require = 0)
    private static void coo$skipNoopItemSync(ItemEntity itemEntity, ItemStack remainder, Operation<Void> original) {
        if (!CoOConfig.farmersdelightSkipNoopItemSync || remainder.getCount() != itemEntity.getItem().getCount()) {
            original.call(itemEntity, remainder);
        }
    }
}
