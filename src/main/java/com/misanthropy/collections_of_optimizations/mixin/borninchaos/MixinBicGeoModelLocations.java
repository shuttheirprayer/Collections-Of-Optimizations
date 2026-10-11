package com.misanthropy.collections_of_optimizations.mixin.borninchaos;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ConstantLocations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = {
        "net.mcreator.borninchaosv.entity.model.BabySkeletonMinionModel",
        "net.mcreator.borninchaosv.entity.model.BabySkeletonModel",
        "net.mcreator.borninchaosv.entity.model.BabySpiderControlledModel",
        "net.mcreator.borninchaosv.entity.model.BabySpiderModel",
        "net.mcreator.borninchaosv.entity.model.BarrelZombieModel",
        "net.mcreator.borninchaosv.entity.model.BloodyGadflyModel",
        "net.mcreator.borninchaosv.entity.model.BoneImpMinionModel",
        "net.mcreator.borninchaosv.entity.model.BoneImpModel",
        "net.mcreator.borninchaosv.entity.model.BonescallerModel",
        "net.mcreator.borninchaosv.entity.model.BonescallerNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.ControlledBabySkeletonModel",
        "net.mcreator.borninchaosv.entity.model.ControlledSpiritualAssistantModel",
        "net.mcreator.borninchaosv.entity.model.CorpseFishModel",
        "net.mcreator.borninchaosv.entity.model.CorpseFlyModel",
        "net.mcreator.borninchaosv.entity.model.DarkVortexModel",
        "net.mcreator.borninchaosv.entity.model.DecayingZombieModel",
        "net.mcreator.borninchaosv.entity.model.DecayingZombieNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.DecrepitSkeletonModel",
        "net.mcreator.borninchaosv.entity.model.DiamondThermiteModel",
        "net.mcreator.borninchaosv.entity.model.DireHoundLeaderModel",
        "net.mcreator.borninchaosv.entity.model.DoorKnightModel",
        "net.mcreator.borninchaosv.entity.model.DoorKnightNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.DreadHoundModel",
        "net.mcreator.borninchaosv.entity.model.DreadHoundNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.FallenChaosKnightModel",
        "net.mcreator.borninchaosv.entity.model.FelsteedModel",
        "net.mcreator.borninchaosv.entity.model.FirelightModel",
        "net.mcreator.borninchaosv.entity.model.FirelightNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.GluttonFishModel",
        "net.mcreator.borninchaosv.entity.model.InfernalSpiritModel",
        "net.mcreator.borninchaosv.entity.model.KrampusHenchmanModel",
        "net.mcreator.borninchaosv.entity.model.KrampusModel",
        "net.mcreator.borninchaosv.entity.model.LifestealerModel",
        "net.mcreator.borninchaosv.entity.model.LifestealerTrueFormModel",
        "net.mcreator.borninchaosv.entity.model.LordPumpkinheadHeadModel",
        "net.mcreator.borninchaosv.entity.model.LordPumpkinheadModel",
        "net.mcreator.borninchaosv.entity.model.LordPumpkinheadWithoutaHorseModel",
        "net.mcreator.borninchaosv.entity.model.LordTheHeadlessModel",
        "net.mcreator.borninchaosv.entity.model.LordsFelsteedModel",
        "net.mcreator.borninchaosv.entity.model.MaggotModel",
        "net.mcreator.borninchaosv.entity.model.MissionaryRaiderModel",
        "net.mcreator.borninchaosv.entity.model.MissionerModel",
        "net.mcreator.borninchaosv.entity.model.MotherSpiderModel",
        "net.mcreator.borninchaosv.entity.model.MrPumpkinControlledModel",
        "net.mcreator.borninchaosv.entity.model.MrPumpkinModel",
        "net.mcreator.borninchaosv.entity.model.MrsPumpkinModel",
        "net.mcreator.borninchaosv.entity.model.NightmareStalkerModel",
        "net.mcreator.borninchaosv.entity.model.PhantomCreeperCopyModel",
        "net.mcreator.borninchaosv.entity.model.PhantomCreeperModel",
        "net.mcreator.borninchaosv.entity.model.PumpkinBombModel",
        "net.mcreator.borninchaosv.entity.model.PumpkinBruiserModel",
        "net.mcreator.borninchaosv.entity.model.PumpkinDunceModel",
        "net.mcreator.borninchaosv.entity.model.PumpkinSpiritModel",
        "net.mcreator.borninchaosv.entity.model.PumpkinheadModel",
        "net.mcreator.borninchaosv.entity.model.RestlessSpiritModel",
        "net.mcreator.borninchaosv.entity.model.RidingFelsteedModel",
        "net.mcreator.borninchaosv.entity.model.RidingLordsFelsteedModel",
        "net.mcreator.borninchaosv.entity.model.ScarletPersecutorModel",
        "net.mcreator.borninchaosv.entity.model.SearedSpiritModel",
        "net.mcreator.borninchaosv.entity.model.SearedSpiritNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.SenorPumpkinModel",
        "net.mcreator.borninchaosv.entity.model.SiameseSkeletonsModel",
        "net.mcreator.borninchaosv.entity.model.SiameseSkeletonsleftModel",
        "net.mcreator.borninchaosv.entity.model.SiameseSkeletonsrightModel",
        "net.mcreator.borninchaosv.entity.model.SirPumpkinheadModel",
        "net.mcreator.borninchaosv.entity.model.SirPumpkinheadWithoutHorseModel",
        "net.mcreator.borninchaosv.entity.model.SirTheHeadlessModel",
        "net.mcreator.borninchaosv.entity.model.SkeletonDemomanModel",
        "net.mcreator.borninchaosv.entity.model.SkeletonThrasherModel",
        "net.mcreator.borninchaosv.entity.model.SkeletonThrasherNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.SpiritGuideAssistantModel",
        "net.mcreator.borninchaosv.entity.model.SpiritGuideModel",
        "net.mcreator.borninchaosv.entity.model.SpiritofChaosModel",
        "net.mcreator.borninchaosv.entity.model.SupremeBonescallerModel",
        "net.mcreator.borninchaosv.entity.model.SupremeBonescallerNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.SupremeBonescallerStage2Model",
        "net.mcreator.borninchaosv.entity.model.SwarmerModel",
        "net.mcreator.borninchaosv.entity.model.ThornshellCrabModel",
        "net.mcreator.borninchaosv.entity.model.ZombieBruiserModel",
        "net.mcreator.borninchaosv.entity.model.ZombieClownModel",
        "net.mcreator.borninchaosv.entity.model.ZombieClownNotDespawnModel",
        "net.mcreator.borninchaosv.entity.model.ZombieFishermanModel",
        "net.mcreator.borninchaosv.entity.model.ZombieLumberjackModel"
}, remap = false)
public abstract class MixinBicGeoModelLocations {

    @Redirect(
            method = {"getModelResource", "getTextureResource", "getAnimationResource"},
            at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            require = 0
    )
    private ResourceLocation coo$reuseLocation(String namespace, String path) {
        return CoOConfig.borninchaosReuseModelLocations
                ? ConstantLocations.of(namespace, path)
                : new ResourceLocation(namespace, path);
    }
}
