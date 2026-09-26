package com.misanthropy.collections_of_optimizations.mixin.chimes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.ChimesPhantomScan;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(targets = "com.cicada.chimes.block.entity.WindBellBE", remap = false)
public abstract class MixinChimesWindBellTick {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
            require = 0
    )
    private <T extends Entity> List<T> coo$phantomScan(Level level, Class<T> type, AABB box, Operation<List<T>> original) {
        if (ChimesPhantomScan.skip(level, ((BlockEntity) (Object) this).getBlockPos())) {
            return Collections.emptyList();
        }
        return original.call(level, type, box);
    }
}
