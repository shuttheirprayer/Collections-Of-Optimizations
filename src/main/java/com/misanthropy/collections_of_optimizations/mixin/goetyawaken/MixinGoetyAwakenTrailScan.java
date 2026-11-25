package com.misanthropy.collections_of_optimizations.mixin.goetyawaken;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(targets = "com.k1sak1.goetyawaken.client.events.ClientTrailHandler", remap = false)
public abstract class MixinGoetyAwakenTrailScan {

    @Unique
    private static final ClassValue<Boolean> coo$own = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                if (c.getName().startsWith("com.k1sak1.goetyawaken.")) {
                    return true;
                }
            }
            return false;
        }
    };

    @Unique
    private static final List<Entity> coo$owned = new ArrayList<>();

    @Unique
    private static WeakReference<ClientLevel> coo$scanned = new WeakReference<>(null);

    @Unique
    private static long coo$stamp = Long.MIN_VALUE;

    @WrapOperation(method = "drawTrails", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;", remap = true), require = 0)
    private static Iterable<Entity> coo$ownEntities(ClientLevel level, Operation<Iterable<Entity>> original) {
        if (!CoOConfig.goetyawakenScanOncePerTick) {
            return original.call(level);
        }
        long now = level.getGameTime();
        if (coo$stamp != now || coo$scanned.get() != level) {
            coo$stamp = now;
            coo$scanned = new WeakReference<>(level);
            coo$owned.clear();
            for (Entity entity : original.call(level)) {
                if (coo$own.get(entity.getClass())) {
                    coo$owned.add(entity);
                }
            }
        } else if (!coo$owned.isEmpty()) {
            coo$owned.removeIf(Entity::isRemoved);
        }
        return coo$owned;
    }
}
