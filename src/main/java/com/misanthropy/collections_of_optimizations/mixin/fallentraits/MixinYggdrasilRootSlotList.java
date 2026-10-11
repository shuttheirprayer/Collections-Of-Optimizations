package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.xkmc.l2hostility.compat.curios.EntitySlotAccess;
import net.kayn.fallen_traits.content.item.curio.YggdrasilRoot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

@Mixin(value = YggdrasilRoot.class, remap = false)
public abstract class MixinYggdrasilRootSlotList {

    @WrapOperation(
            method = "curioTick",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/xkmc/l2hostility/compat/curios/CurioCompat;getItemAccess(Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<EntitySlotAccess> coo$skipUnusedSlotList(LivingEntity entity, Operation<List<EntitySlotAccess>> original,
                                                         SlotContext slotContext, ItemStack stack) {
        if (CoOConfig.fallentraitsSkipIdleRootSlotList) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("YggdrasilRoot") || stack.getDamageValue() + 1 >= stack.getMaxDamage()) {
                return List.of();
            }
        }
        return original.call(entity);
    }
}
