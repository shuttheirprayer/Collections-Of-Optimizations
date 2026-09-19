package com.misanthropy.collections_of_optimizations.mixin.hazennstuff;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = {
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.DedicatedArmorSets.AtlasArmor.AtlasArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.DedicatedArmorSets.FireblossomBattlemage.Crown.FireblossomBattlemageCrownArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.DedicatedArmorSets.FireblossomBattlemage.FireblossomBattlemageArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.DedicatedArmorSets.FireblossomBattlemage.Helmet.FireblossomBattlemageHelmetArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.MithrilTier.MithrilBattlemage.MithrilBattlemageArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.AlchemistSupreme.AlchemistSupremeArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.Blazeborne.BlazeborneArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.CreakingSorcerer.CreakingSorcererArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.CryogenicRuler.CryogenicRulerArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.EnderDragon.EnderDragonArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.FleshMass.FleshMassArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.Hazel.BigHazelArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.Hazel.HazelArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.Infestation.InfestationArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.Seraph.SeraphArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PureTier.ThunderProwler.ThunderProwlerArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PyriumTier.LegionnaireCommander.LegionnaireCommanderArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PyriumTier.LegionnaireRuler.LegionnaireRulerArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PyriumTier.Pyrium.PyriumArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.ArmorSets.PyriumTier.PyriumBattlemage.PyriumBattlemageArmorModel",
        "net.hazen.hazennstuff.Items.Equipment.Staves.BlossomOfTheEnchantress.BlossomOfTheEnchantressModel",
        "net.hazen.hazennstuff.Items.Equipment.Staves.DivineScepter.DivineScepterModel",
        "net.hazen.hazennstuff.Items.Equipment.Staves.EnderconicScepter.EnderconicScepterModel",
        "net.hazen.hazennstuff.Items.Equipment.Staves.GrimoireStaff.GrimoireStaffModel",
        "net.hazen.hazennstuff.Items.Equipment.Staves.InsaniaAeternum.InsaniaAeternusModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.Devastator.DevastatorModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.DragonSplitter.DraconicSplitterModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.Excalibur.ExcaliburModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.FireblossomRapier.FireblossomRapierModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.IcePike.IcePikeModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.IonicSplitter.IonicSplitterModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.LegionnaireWarAxe.LegionnaireWarlockAxeModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.OFortuna.OFortunaModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.Provocation.ProvocationModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.RavensBane.RavensBaneModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.TheDevourer.TheDevourerModel",
        "net.hazen.hazennstuff.Items.Equipment.Weapons.Umbranova.UmbranovaModel"
}, remap = false)
public abstract class MixinHnSGeoModelResources {

    @Unique
    private ResourceLocation coo$modelResource;

    @Unique
    private ResourceLocation coo$textureResource;

    @Unique
    private ResourceLocation coo$animationResource;

    @WrapOperation(
            method = "getModelResource",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private ResourceLocation coo$cacheModelResource(String namespace, String path, Operation<ResourceLocation> original) {
        if (!CoOConfig.hazennstuffCacheGeoResources) {
            return original.call(namespace, path);
        }
        ResourceLocation cached = this.coo$modelResource;
        if (cached == null) {
            cached = original.call(namespace, path);
            this.coo$modelResource = cached;
        }
        return cached;
    }

    @WrapOperation(
            method = "getTextureResource",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private ResourceLocation coo$cacheTextureResource(String namespace, String path, Operation<ResourceLocation> original) {
        if (!CoOConfig.hazennstuffCacheGeoResources) {
            return original.call(namespace, path);
        }
        ResourceLocation cached = this.coo$textureResource;
        if (cached == null) {
            cached = original.call(namespace, path);
            this.coo$textureResource = cached;
        }
        return cached;
    }

    @WrapOperation(
            method = "getAnimationResource",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private ResourceLocation coo$cacheAnimationResource(String namespace, String path, Operation<ResourceLocation> original) {
        if (!CoOConfig.hazennstuffCacheGeoResources) {
            return original.call(namespace, path);
        }
        ResourceLocation cached = this.coo$animationResource;
        if (cached == null) {
            cached = original.call(namespace, path);
            this.coo$animationResource = cached;
        }
        return cached;
    }
}
