package com.misanthropy.collections_of_optimizations.mixin.goety;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityGoetyHooks extends Entity {

    @Shadow @Nullable private LivingEntity lastHurtByMob;
    @Shadow protected int lastHurtByPlayerTime;
    @Shadow public abstract boolean hasEffect(MobEffect effect);
    @Shadow public abstract MobType getMobType();
    @Shadow public abstract float getMaxHealth();
    @Shadow public abstract boolean wasExperienceConsumed();
    @Shadow public abstract int getExperienceReward();
    @Shadow protected abstract boolean isAlwaysExperienceDropper();
    @Shadow public abstract Map<MobEffect, MobEffectInstance> getActiveEffectsMap();

    protected MixinLivingEntityGoetyHooks(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyReturnValue(method = "hasEffect", at = @At("RETURN"))
    private boolean coo$acidVenomCountsAsPoison(boolean original, MobEffect effect) {
        return original || (effect == MobEffects.POISON && getActiveEffectsMap().containsKey(GoetyEffects.ACID_VENOM.get()));
    }

    @ModifyReturnValue(method = "getMobType", at = @At("RETURN"))
    private MobType coo$lichIsUndead(MobType original) {
        return LichdomHelper.isLich(this) ? MobType.UNDEAD : original;
    }

    @Inject(method = "dropExperience", at = @At("HEAD"))
    private void coo$servantKillsDropExperience(CallbackInfo ci) {
        if (this.level() instanceof ServerLevel serverLevel
                && this.lastHurtByPlayerTime <= 0
                && !this.isAlwaysExperienceDropper()
                && this.lastHurtByMob instanceof IOwned owned
                && !this.wasExperienceConsumed()
                && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)
                && owned.getMasterOwner() instanceof Player player) {
            int reward = ForgeEventFactory.getExperienceDrop((LivingEntity) (Object) this, player, this.getExperienceReward());
            ExperienceOrb.award(serverLevel, this.position(), reward);
        }
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void coo$lichUndeadFriends(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (MainConfig.LichUndeadFriends.get()
                && (this.getMobType() == MobType.UNDEAD || this.getType().is(ModTags.EntityTypes.LICH_NEUTRAL))
                && LichdomHelper.isLich(target)) {
            if (!MainConfig.LichPowerfulFoes.get() || this.getMaxHealth() <= MainConfig.LichPowerfulFoesHealth.get()) {
                cir.setReturnValue(false);
            }
        }
    }

    @ModifyReturnValue(method = "isSensitiveToWater", at = @At("RETURN"))
    private boolean coo$snowSkinSensitiveToWater(boolean original) {
        return original || this.hasEffect(GoetyEffects.SNOW_SKIN.get());
    }

    @Inject(method = "randomTeleport", at = @At("HEAD"), cancellable = true)
    private void coo$enderGroundBlocksTeleport(double x, double y, double z, boolean particles, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(GoetyEffects.ENDER_GROUND.get())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void coo$stunnedCannotJump(CallbackInfo ci) {
        if (this.hasEffect(GoetyEffects.STUNNED.get()) || this.hasEffect(GoetyEffects.TANGLED.get())) {
            ci.cancel();
        }
    }

    @Inject(method = "travelRidden", at = @At("HEAD"), cancellable = true)
    private void coo$autonomousMountIgnoresRider(Player player, Vec3 input, CallbackInfo ci) {
        if (this instanceof IAutoRideable rideable && rideable.isAutonomous()) {
            ci.cancel();
        }
    }

    @Inject(method = "updateInvisibilityStatus", at = @At("TAIL"))
    private void coo$shadowWalkInvisible(CallbackInfo ci) {
        if (this.hasEffect(GoetyEffects.SHADOW_WALK.get())) {
            this.setInvisible(true);
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void coo$alliesDoNotHarmServants(MobEffectInstance instance, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (source instanceof IOwned
                && !MobsConfig.ServantsHarmEffectApply.get()
                && instance.getEffect().getCategory() == MobEffectCategory.HARMFUL
                && MobUtil.areAllies(this, source)) {
            cir.setReturnValue(false);
        }
    }
}
