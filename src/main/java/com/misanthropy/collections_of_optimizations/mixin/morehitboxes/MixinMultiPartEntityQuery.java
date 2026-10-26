package com.misanthropy.collections_of_optimizations.mixin.morehitboxes;

import com.bawnorton.mixinsquared.TargetHandler;
import com.github.darkpred.morehitboxes.api.MultiPart;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Level.class, priority = 1500)
public abstract class MixinMultiPartEntityQuery {

    @WrapWithCondition(
            method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;Ljava/util/List;)V",
                    remap = false
            ),
            remap = true,
            require = 0,
            expect = 0
    )
    private boolean coo$skipAbsentMultiPartFilter(Level level, Entity entity, AABB box, Predicate<Entity> predicate,
                                                  CallbackInfoReturnable<List<Entity>> cir, List<Entity> hits) {
        if (!CoOConfig.morehitboxesSkipAbsentMultiPartFilter) {
            return true;
        }

        for (int i = 0, n = hits.size(); i < n; i++) {
            if (hits.get(i) instanceof MultiPart) {
                return true;
            }
        }

        return false;
    }

    @TargetHandler(mixin = "com.github.darkpred.morehitboxes.mixin.LevelMixin", name = "addMultiPartsToEntityQuery")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private <T extends Entity> void coo$boxBeforeContains(EntityTypeTest<Entity, T> test, AABB area,
                                                          Predicate<? super T> predicate,
                                                          CallbackInfoReturnable<List<T>> cir, List<Entity> list,
                                                          CallbackInfo ci) {
        if (!CoOConfig.morehitboxesBoxFirstPartQuery) {
            return;
        }
        ci.cancel();
        for (PartEntity<?> part : ((Level) (Object) this).getPartEntities()) {
            if (part instanceof MultiPart && part.getBoundingBox().intersects(area)) {
                T parent = test.tryCast(part.getParent());
                if (parent != null && !list.contains(parent) && predicate.test(parent)) {
                    list.add(parent);
                }
            }
        }
    }
}
