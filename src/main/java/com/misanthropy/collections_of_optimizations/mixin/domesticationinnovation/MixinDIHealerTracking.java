package com.misanthropy.collections_of_optimizations.mixin.domesticationinnovation;

import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe668.domesticationinnovation.server.CommonProxy;
import com.misanthropy.collections_of_optimizations.core.DomesticationHealers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class MixinDIHealerTracking {
    private MixinDIHealerTracking() {
    }

    @Mixin(value = CommonProxy.class, remap = false)
    public abstract static class Join {
        @Inject(method = "onEntityJoinWorldEvent", at = @At("HEAD"))
        private void coo$trackJoiningHealer(EntityJoinLevelEvent event, CallbackInfo ci) {
            DomesticationHealers.track(event.getEntity());
        }
    }

    @Mixin(value = CitadelEntityData.class, remap = false)
    public abstract static class TagSync {
        @Inject(method = "setCitadelTag", at = @At("RETURN"))
        private static void coo$trackTaggedHealer(LivingEntity entity, CompoundTag tag, CallbackInfo ci) {
            DomesticationHealers.track(entity);
        }
    }
}
