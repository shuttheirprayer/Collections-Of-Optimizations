package com.misanthropy.collections_of_optimizations.mixin.cablefacades;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.core.CableFacadeGuard;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 1500)
public abstract class MixinCableFacadesGuardSkip {

    @TargetHandler(
            mixin = "com.portingdeadmods.cable_facades.mixins.BlockStateBaseMixin",
            name = "getCollisionShape"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;get()Ljava/lang/Object;"),
            require = 0,
            remap = false
    )
    private Object coo$skipGetCollisionShapeGuard(ThreadLocal<?> guard, @Local(argsOnly = true) BlockGetter getter) {
        return CableFacadeGuard.get(guard, getter);
    }

    @TargetHandler(
            mixin = "com.portingdeadmods.cable_facades.mixins.BlockStateBaseMixin",
            name = "getShape"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;get()Ljava/lang/Object;"),
            require = 0,
            remap = false
    )
    private Object coo$skipGetShapeGuard(ThreadLocal<?> guard, @Local(argsOnly = true) BlockGetter getter) {
        return CableFacadeGuard.get(guard, getter);
    }

    @TargetHandler(
            mixin = "com.portingdeadmods.cable_facades.mixins.BlockStateBaseMixin",
            name = "getOcclusionShape"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;get()Ljava/lang/Object;"),
            require = 0,
            remap = false
    )
    private Object coo$skipGetOcclusionShapeGuard(ThreadLocal<?> guard, @Local(argsOnly = true) BlockGetter getter) {
        return CableFacadeGuard.get(guard, getter);
    }

    @TargetHandler(
            mixin = "com.portingdeadmods.cable_facades.mixins.BlockStateBaseMixin",
            name = "getLightBlock"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;get()Ljava/lang/Object;"),
            require = 0,
            remap = false
    )
    private Object coo$skipGetLightBlockGuard(ThreadLocal<?> guard, @Local(argsOnly = true) BlockGetter getter) {
        return CableFacadeGuard.get(guard, getter);
    }

    @TargetHandler(
            mixin = "com.portingdeadmods.cable_facades.mixins.BlockStateBaseMixin",
            name = "propagatesSkylightDown"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;get()Ljava/lang/Object;"),
            require = 0,
            remap = false
    )
    private Object coo$skipPropagatesSkylightDownGuard(ThreadLocal<?> guard, @Local(argsOnly = true) BlockGetter getter) {
        return CableFacadeGuard.get(guard, getter);
    }

    @TargetHandler(
            mixin = "com.portingdeadmods.cable_facades.mixins.BlockStateBaseMixin",
            name = "isSolidRender"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;get()Ljava/lang/Object;"),
            require = 0,
            remap = false
    )
    private Object coo$skipIsSolidRenderGuard(ThreadLocal<?> guard, @Local(argsOnly = true) BlockGetter getter) {
        return CableFacadeGuard.get(guard, getter);
    }
}
