package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.items.Monstrous_Helm;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(value = Monstrous_Helm.class, remap = false)
public abstract class MixinCataclysmMonstrousHelm {

    @WrapOperation(
            method = "onArmorTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45933_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Entity> coo$scanOnlyWhenBerserk(Level level, Entity except, AABB box, Operation<List<Entity>> original,
                                                ItemStack stack, Level world, Player player) {
        if (CoOConfig.cataclysmLazyMonstrousHelmScan
                && !(player.getMaxHealth() * 1.0F / 2.0F >= player.getHealth()
                && !player.getCooldowns().isOnCooldown((Item) (Object) this))) {
            return Collections.emptyList();
        }
        return original.call(level, except, box);
    }
}
