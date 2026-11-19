package com.misanthropy.collections_of_optimizations.mixin.dungeoncrawl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(targets = "xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock$PropertyHolder", remap = false)
public abstract class MixinDungeonModelPropertyHolder {

    @Shadow
    public String valueName;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @WrapOperation(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;m_61124_(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private Object coo$resolveRacedValue(BlockState state, Property property, Comparable value, Operation<Object> original) {
        if (value == null) {
            Optional<?> parsed = property.getValue(valueName);
            if (parsed.isEmpty()) {
                return state;
            }
            value = (Comparable) parsed.get();
        }
        return original.call(state, property, value);
    }
}
