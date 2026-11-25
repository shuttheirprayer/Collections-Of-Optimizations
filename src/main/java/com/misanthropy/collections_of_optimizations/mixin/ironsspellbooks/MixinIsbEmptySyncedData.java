package com.misanthropy.collections_of_optimizations.mixin.ironsspellbooks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientMagicData.class, remap = false)
public abstract class MixinIsbEmptySyncedData {

    @Shadow
    @Final
    private static SyncedSpellData emptySyncedData;

    @Unique
    private static final ClassValue<Boolean> coo$magic = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            return IMagicEntity.class.isAssignableFrom(type);
        }
    };

    @Redirect(method = "getSyncedSpellData", at = @At(value = "CONSTANT", args = "classValue=io/redspace/ironsspellbooks/api/entity/IMagicEntity", ordinal = 0), require = 0)
    private static boolean coo$isMagic(Object entity, Class<?> type) {
        return entity != null && (CoOConfig.ironsspellbooksShareEmptySyncedData ? coo$magic.get(entity.getClass()) : type.isInstance(entity));
    }

    @Redirect(method = "getSyncedSpellData", at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/LivingEntity;)Lio/redspace/ironsspellbooks/capabilities/magic/SyncedSpellData;"), require = 0)
    private static SyncedSpellData coo$shareEmptyData(LivingEntity entity) {
        return CoOConfig.ironsspellbooksShareEmptySyncedData ? emptySyncedData : new SyncedSpellData(entity);
    }
}
