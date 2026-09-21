package com.misanthropy.collections_of_optimizations.mixin.undergarden;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "quek.undergarden.entity.rotspawn.RotspawnMonster", remap = false)
public abstract class MixinUgRotspawnRepelScan {

    @ModifyExpressionValue(
            method = "m_8107_",
            at = @At(
                    value = "FIELD",
                    target = "f_19797_:I"
            ),
            require = 0
    )
    private int coo$staggerRepelScan(int original) {
        if (!CoOConfig.undergardenStaggerRotspawnRepelScan) {
            return original;
        }
        Entity self = (Entity) (Object) this;
        return original + (self.getId() & 31);
    }

    @WrapOperation(
            method = "m_8107_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;m_121930_(Lnet/minecraft/core/BlockPos;IILjava/util/function/Predicate;)Ljava/util/Optional;"
            ),
            remap = false,
            require = 0
    )
    private Optional coo$gateRepelScan(BlockPos origin, int horizontal, int vertical, Predicate filter, Operation<Optional> original) {
        int radius = CoOConfig.undergardenRotspawnRepelScanRadius;
        if (radius <= 0) {
            return original.call(origin, horizontal, vertical, filter);
        }
        Entity self = (Entity) (Object) this;
        Level level = self.level();
        if (level == null || level.isClientSide()) {
            return original.call(origin, horizontal, vertical, filter);
        }
        if (!level.hasNearbyAlivePlayer(self.getX(), self.getY(), self.getZ(), radius)) {
            return Optional.empty();
        }
        return original.call(origin, horizontal, vertical, filter);
    }
}
