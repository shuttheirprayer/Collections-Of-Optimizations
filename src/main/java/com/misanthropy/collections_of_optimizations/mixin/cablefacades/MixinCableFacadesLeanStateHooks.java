package com.misanthropy.collections_of_optimizations.mixin.cablefacades;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CableFacadePresence;
import com.portingdeadmods.cable_facades.CFConfig;
import com.portingdeadmods.cable_facades.api.facade_type.FacadeTypes;
import com.portingdeadmods.cable_facades.content.items.DirectionalFacadeItem;
import com.portingdeadmods.cable_facades.content.items.FacadeItem;
import com.portingdeadmods.cable_facades.data.FacadeData;
import com.portingdeadmods.cable_facades.multipart.MultipartShapeBuilder;
import com.portingdeadmods.cable_facades.registries.CFItems;
import com.portingdeadmods.cable_facades.utils.FacadeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinCableFacadesLeanStateHooks {

    @Unique
    private static final ThreadLocal<Boolean> coo$facadeGuard = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Shadow
    public abstract Block getBlock();

    @Unique
    private static FacadeData coo$facade(BlockGetter getter, BlockPos pos) {
        if (getter == null) {
            return null;
        }
        if (CoOConfig.cablefacadesSkipGuardWhenEmpty && !(getter instanceof ServerLevel) && !CableFacadePresence.any()) {
            return null;
        }
        if (coo$facadeGuard.get()) {
            return null;
        }
        coo$facadeGuard.set(Boolean.TRUE);
        try {
            return FacadeUtils.getFacadeData(getter, pos);
        } finally {
            coo$facadeGuard.set(Boolean.FALSE);
        }
    }

    @Unique
    private static <T> T coo$guarded(Supplier<T> body) {
        coo$facadeGuard.set(Boolean.TRUE);
        try {
            return body.get();
        } finally {
            coo$facadeGuard.set(Boolean.FALSE);
        }
    }

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void coo$facadeOnRemove(Level level, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if (coo$facadeGuard.get()) {
            return;
        }
        coo$facadeGuard.set(Boolean.TRUE);
        try {
            FacadeData data = FacadeUtils.getFacadeData(level, pos);
            if (data == null || newState.is(this.getBlock())) {
                return;
            }
            if (level instanceof ServerLevel) {
                if (data.isFullBlock()) {
                    FacadeItem fullItem = FacadeTypes.fullItemFor(data.facadeType());
                    if (fullItem == null) {
                        fullItem = CFItems.FACADE.get();
                    }
                    ItemStack stack = fullItem.createFacade(data.getFullBlock().getBlock());
                    FacadeUtils.removeFacade(level, pos);
                    if (CFConfig.consumeFacade) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                } else if (data.isDirectional()) {
                    ResourceLocation typeId = data.facadeType();
                    DirectionalFacadeItem lookup = FacadeTypes.directionalItemFor(typeId);
                    DirectionalFacadeItem item = lookup != null ? lookup : CFItems.DIRECTIONAL_FACADE.get();
                    FacadeUtils.removeFacade(level, pos);
                    if (CFConfig.consumeFacade) {
                        data.directional().forEach((direction, state) -> Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item.createFacade(state.getBlock())));
                    }
                }
            }
            FacadeUtils.updateBlocks(level, pos);
        } finally {
            coo$facadeGuard.set(Boolean.FALSE);
        }
    }

    @ModifyReturnValue(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
    private VoxelShape coo$facadeCollisionShape(VoxelShape original, @Local(argsOnly = true) BlockGetter getter, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) CollisionContext context) {
        FacadeData data = coo$facade(getter, pos);
        if (data == null) {
            return original;
        }
        BlockState self = (BlockState) (Object) this;
        return coo$guarded(() -> MultipartShapeBuilder.buildCollisionShape(getter, pos, self, data, context));
    }

    @ModifyReturnValue(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
    private VoxelShape coo$facadeShape(VoxelShape original, @Local(argsOnly = true) BlockGetter getter, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) CollisionContext context) {
        FacadeData data = coo$facade(getter, pos);
        if (data == null) {
            return original;
        }
        BlockState self = (BlockState) (Object) this;
        return coo$guarded(() -> MultipartShapeBuilder.buildShape(getter, pos, self, data, context));
    }

    @ModifyReturnValue(method = "getOcclusionShape", at = @At("RETURN"))
    private VoxelShape coo$facadeOcclusionShape(VoxelShape original, @Local(argsOnly = true) BlockGetter getter, @Local(argsOnly = true) BlockPos pos) {
        FacadeData data = coo$facade(getter, pos);
        if (data == null) {
            return original;
        }
        return coo$guarded(() -> MultipartShapeBuilder.buildOcclusionShape(getter, pos, data));
    }

    @ModifyReturnValue(method = "getLightBlock", at = @At("RETURN"))
    private int coo$facadeLightBlock(int original, @Local(argsOnly = true) BlockGetter getter, @Local(argsOnly = true) BlockPos pos) {
        FacadeData data = coo$facade(getter, pos);
        if (data == null) {
            return original;
        }
        return data.isFullBlock() ? coo$guarded(() -> data.getFullBlock().getLightBlock(getter, BlockPos.ZERO)) : 0;
    }

    @ModifyReturnValue(method = "propagatesSkylightDown", at = @At("RETURN"))
    private boolean coo$facadeSkylight(boolean original, @Local(argsOnly = true) BlockGetter getter, @Local(argsOnly = true) BlockPos pos) {
        FacadeData data = coo$facade(getter, pos);
        if (data == null) {
            return original;
        }
        return !data.isFullBlock() || coo$guarded(() -> data.getFullBlock().propagatesSkylightDown(getter, BlockPos.ZERO));
    }

    @ModifyReturnValue(method = "isSolidRender", at = @At("RETURN"))
    private boolean coo$facadeSolidRender(boolean original, @Local(argsOnly = true) BlockGetter getter, @Local(argsOnly = true) BlockPos pos) {
        FacadeData data = coo$facade(getter, pos);
        if (data == null) {
            return original;
        }
        return data.isFullBlock() && coo$guarded(() -> data.getFullBlock().isSolidRender(getter, BlockPos.ZERO));
    }
}
