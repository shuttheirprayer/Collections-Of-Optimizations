package com.misanthropy.collections_of_optimizations.mixin.terramity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ProcedureClipMemo;
import net.mcreator.terramity.procedures.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {
        ChaosHeartRingTeleportProcedure.class,
        CirceCastProcedure.class,
        CirceTeleportProcedure.class,
        ConductiteScouterHelmetTickEventProcedure.class,
        DimensionalPoofPoofProcedure.class,
        DimliteArmorSetAbilityProcedure.class,
        DragonBandAbilityProcedure.class,
        DuskrokSwingProcedure.class,
        FinalKamehamehaOnEntityTickUpdateProcedure.class,
        GalickGunOnEntityTickUpdateProcedure.class,
        GobProjectileProcedure.class,
        GobPunchProcedure.class,
        GobSlamSummoningProcedure.class,
        GobSummonProcedure.class,
        GuardianGrimoireRightclickedProcedure.class,
        GuidedEnergyBlastRightclickedProcedure.class,
        GundalfPlanetBusterProcedure.class,
        GundalfShotgunProcedure.class,
        GundalfSniperProcedure.class,
        HellrokBuffParticlesProcedure.class,
        HellrokSlamAttackProcedure.class,
        MicrowaveLaserProcedure.class,
        MurasamaRightclickedProcedure.class,
        PlanetBusterLaserProcedure.class,
        RailgunLaserProcedure.class,
        ReveriumArmorAbilityProcProcedure.class,
        SacrificialSashVanishProcedure.class,
        ShadowflameSashVanishProcedure.class,
        SmallMuzzleSmokeCloudProcedure.class,
        SnifferKamehamehaOnEntityTickUpdateProcedure.class,
        SuperSnifferPeltProcProcedure.class,
        SuperSnifferSlamProcedure.class,
        SuperSnifferStompProcedure.class,
        TGBreathProcedure.class,
        TGEffigySummonProcedure.class,
        TGHandOnEntityTickUpdateProcedure.class,
        TGLaserTwoProcedure.class,
        TrialGuardianAttackSummonProcedure.class,
        UltraSnifferCounterProcedure.class,
        UltraSnifferCounterRandomCallProcedure.class,
        UltraSnifferFlipProcedure.class,
        UltraSnifferFlipRandomCallProcedure.class,
        UltraSnifferSlamProcedure.class,
        UltraSnifferStompProcedure.class,
        UltraSniffersPeltVanishProcedure.class,
        UvogreSlamProcedure.class,
        UvogreSwingProcedure.class,
        VoidGlassesWhileBaubleIsEquippedTickProcedure.class,
        WaterStreamProcedure.class
}, remap = false)
public abstract class MixinTerramityProcedureRaycasts {

    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;",
                    remap = true
            ),
            remap = true,
            require = 0
    )
    private static BlockHitResult coo$reuseIdenticalClip(Level level, ClipContext context, Operation<BlockHitResult> original) {
        if (!CoOConfig.terramityMemoizeProcedureRaycasts) {
            return original.call(level, context);
        }

        ClipContextAccessor fields = (ClipContextAccessor) (Object) context;
        CollisionContext collision = fields.coo$collisionContext();
        Object owner = collision instanceof EntityCollisionContext entityContext ? entityContext.getEntity() : collision;
        Vec3 from = context.getFrom();
        Vec3 to = context.getTo();
        ClipContext.Block block = fields.coo$block();
        ClipContext.Fluid fluid = fields.coo$fluid();

        ProcedureClipMemo memo = ProcedureClipMemo.local();
        BlockHitResult cached = memo.hit(level, from, to, block, fluid, owner);
        if (cached != null) {
            return cached;
        }

        BlockHitResult result = original.call(level, context);
        memo.store(level, from, to, block, fluid, owner, result);
        return result;
    }
}
