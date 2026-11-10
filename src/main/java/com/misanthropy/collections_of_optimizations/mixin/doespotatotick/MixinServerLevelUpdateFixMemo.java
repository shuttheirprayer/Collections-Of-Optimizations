package com.misanthropy.collections_of_optimizations.mixin.doespotatotick;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.EntityNbtMemo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerLevel.class, priority = 1500)
public abstract class MixinServerLevelUpdateFixMemo {

    @TargetHandler(
            mixin = "me.kall.doespotatotick.mixin.impl.ServerLevelMixin",
            name = "onTick"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;m_128471_(Ljava/lang/String;)Z"),
            require = 0,
            remap = false
    )
    private boolean coo$rememberUpdateFix(CompoundTag data, String key, Operation<Boolean> original, @Local(argsOnly = true) Entity entity) {
        if (!CoOConfig.doespotatotickRememberUpdateFix) {
            return original.call(data, key);
        }
        EntityNbtMemo memo = (EntityNbtMemo) entity;
        if (memo.coo$updateFixedTag() == data) {
            return true;
        }
        boolean fixed = original.call(data, key);
        if (fixed) {
            memo.coo$updateFixedTag(data);
        }
        return fixed;
    }
}
