package com.misanthropy.collections_of_optimizations.mixin.traveloptics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ConstantLocations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = {
        "com.gametechbc.traveloptics.entity.armor.abyssal_hide_armor.AbyssalHideArmorModel",
        "com.gametechbc.traveloptics.entity.armor.cursed_wraithguard_armor.CursedWraithguardArmorModel",
        "com.gametechbc.traveloptics.entity.armor.deepling_mage_armor.DeeplingMageArmorModel",
        "com.gametechbc.traveloptics.entity.armor.forlorn_harbinger.ForlornHarbingerArmorModel",
        "com.gametechbc.traveloptics.entity.armor.mechanized_exoskeleton_armor.MechanizedExoskeletonModel",
        "com.gametechbc.traveloptics.entity.armor.primordial_crest_armor.PrimordialCrestArmorModel",
        "com.gametechbc.traveloptics.entity.armor.riptide_sovereign.RiptideSovereignArmorModel",
        "com.gametechbc.traveloptics.entity.armor.tectonic_crest_armor.TectonicCrestArmorModel",
        "com.gametechbc.traveloptics.entity.item.abyssal_tidecaller.base.AbyssalTidecallerModel",
        "com.gametechbc.traveloptics.entity.item.abyssal_tidecaller.evo_one.AbyssalTidecallerLevelOneModel",
        "com.gametechbc.traveloptics.entity.item.abyssal_tidecaller.evo_three.AbyssalTidecallerLevelThreeModel",
        "com.gametechbc.traveloptics.entity.item.abyssal_tidecaller.evo_two.AbyssalTidecallerLevelTwoModel",
        "com.gametechbc.traveloptics.entity.item.charged_sands.ChargedSandsItemModel",
        "com.gametechbc.traveloptics.entity.item.cursed_wraithblade.CursedWraithbladeItemModel",
        "com.gametechbc.traveloptics.entity.item.flames_of_eldritch.base.FlamesOfEldritchModel",
        "com.gametechbc.traveloptics.entity.item.flames_of_eldritch.evo_one.FlamesOfEldritchLevelOneModel",
        "com.gametechbc.traveloptics.entity.item.flames_of_eldritch.evo_three.FlamesOfEldritchLevelThreeModel",
        "com.gametechbc.traveloptics.entity.item.flames_of_eldritch.evo_two.FlamesOfEldritchLevelTwoModel",
        "com.gametechbc.traveloptics.entity.item.galenic_polarizer.GalenicPolarizerModel",
        "com.gametechbc.traveloptics.entity.item.gauntlet_of_extinction.GauntletOfExtinctionItemModel",
        "com.gametechbc.traveloptics.entity.item.infernal_devastator.InfernalDevastatorModel",
        "com.gametechbc.traveloptics.entity.item.lament_of_the_moon.LamentOfTheMoonModel",
        "com.gametechbc.traveloptics.entity.item.mechanized_wraithblade.MechanizedWraithbladeItemModel",
        "com.gametechbc.traveloptics.entity.item.reckoning_of_the_eternal_hunger.ReckoningOfTheEternalHungerModel",
        "com.gametechbc.traveloptics.entity.item.scourge_of_the_sands.base.ScourgeOfTheSandsModel",
        "com.gametechbc.traveloptics.entity.item.scourge_of_the_sands.evo_one.ScourgeOfTheSandsLevelOneModel",
        "com.gametechbc.traveloptics.entity.item.scourge_of_the_sands.evo_three.ScourgeOfTheSandsLevelThreeModel",
        "com.gametechbc.traveloptics.entity.item.scourge_of_the_sands.evo_two.ScourgeOfTheSandsLevelTwoModel",
        "com.gametechbc.traveloptics.entity.item.staff_of_the_storm_empress.StaffOfTheStormEmpressModel",
        "com.gametechbc.traveloptics.entity.item.stellothorn.StellothornModel",
        "com.gametechbc.traveloptics.entity.item.the_obliterator.base.TheObliteratorModel",
        "com.gametechbc.traveloptics.entity.item.the_obliterator.evo_one.TheObliteratorLevelOneModel",
        "com.gametechbc.traveloptics.entity.item.the_obliterator.evo_three.TheObliteratorLevelThreeModel",
        "com.gametechbc.traveloptics.entity.item.the_obliterator.evo_two.TheObliteratorLevelTwoModel",
        "com.gametechbc.traveloptics.entity.item.thorns_of_oblivion.base.ThornsOfOblivionModel",
        "com.gametechbc.traveloptics.entity.item.thorns_of_oblivion.evo_one.ThornsOfOblivionLevelOneModel",
        "com.gametechbc.traveloptics.entity.item.thorns_of_oblivion.evo_three.ThornsOfOblivionLevelThreeModel",
        "com.gametechbc.traveloptics.entity.item.thorns_of_oblivion.evo_two.ThornsOfOblivionLevelTwoModel",
        "com.gametechbc.traveloptics.entity.item.titanlord_scepter.base.TitanlordScepterModel",
        "com.gametechbc.traveloptics.entity.item.titanlord_scepter.retro.TitanlordScepterRetroModel",
        "com.gametechbc.traveloptics.entity.item.titanlord_scepter.tectonic.TitanlordScepterTectonicModel",
        "com.gametechbc.traveloptics.entity.item.trident_of_the_eternal_maelstrom.EternalMaelstromTridentItemModel",
        "com.gametechbc.traveloptics.entity.item.voidstrike_reaper.base.VoidstrikeReaperModel",
        "com.gametechbc.traveloptics.entity.item.voidstrike_reaper.evo_one.VoidstrikeReaperLevelOneModel",
        "com.gametechbc.traveloptics.entity.item.voidstrike_reaper.evo_three.VoidstrikeReaperLevelThreeModel",
        "com.gametechbc.traveloptics.entity.item.voidstrike_reaper.evo_two.VoidstrikeReaperLevelTwoModel",
        "com.gametechbc.traveloptics.entity.item.wand_of_final_light.WandOfFinalLightModel",
        "com.gametechbc.traveloptics.entity.misc.lab_computer.LabComputerModel",
        "com.gametechbc.traveloptics.entity.mobs.fox_confluct_statue.FoxConfluctStatueModel",
        "com.gametechbc.traveloptics.entity.mobs.nightwarden_boss.NightwardenBossModel",
        "com.gametechbc.traveloptics.entity.mobs.snow_fox.SnowFoxModel",
        "com.gametechbc.traveloptics.entity.mobs.voidshelf_golem.VoidshelfGolemModel"
}, remap = false)
public abstract class MixinTravelopticsGeoModelLocations {

    @WrapOperation(
            method = {"getModelResource", "getTextureResource", "getAnimationResource"},
            at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            require = 0
    )
    private ResourceLocation coo$reuseLocation(String namespace, String path, Operation<ResourceLocation> original) {
        return CoOConfig.travelopticsReuseModelLocations
                ? ConstantLocations.of(namespace, path)
                : original.call(namespace, path);
    }
}
