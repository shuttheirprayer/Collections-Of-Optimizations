package com.misanthropy.collections_of_optimizations.mixin.goety;

import com.Polarice3.Goety.client.events.EnchantBeamEvent;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EnchantBeamEvent.class, remap = false)
public abstract class MixinEnchantBeamEventTickList {

    @Unique
    private static final List<Entity> coo$enchanted = new ArrayList<>();
    @Unique
    private static ClientLevel coo$level;
    @Unique
    private static long coo$tick = Long.MIN_VALUE;

    @WrapOperation(method = "renderBeams", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;", remap = true), require = 0)
    private static Iterable<Entity> coo$onlyEnchanted(ClientLevel level, Operation<Iterable<Entity>> original) {
        if (!CoOConfig.goetyTickListEnchantBeams) {
            return original.call(level);
        }
        long tick = level.getGameTime();
        if (tick != coo$tick || level != coo$level) {
            coo$tick = tick;
            coo$level = level;
            coo$enchanted.clear();
            for (Entity entity : original.call(level)) {
                if (entity instanceof LivingEntity living && !living.isRemoved() && MiscCapHelper.getEnchanter(living) != null) {
                    coo$enchanted.add(entity);
                }
            }
        }
        return coo$enchanted;
    }
}
