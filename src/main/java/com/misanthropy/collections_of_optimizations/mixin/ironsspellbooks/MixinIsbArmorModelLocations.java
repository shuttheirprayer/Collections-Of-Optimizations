package com.misanthropy.collections_of_optimizations.mixin.ironsspellbooks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ConstantLocations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = {
        "io.redspace.ironsspellbooks.entity.armor.ArchevokerArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.BootsOfSpeedArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.CryomancerArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.CultistArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.ElectromancerArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.GoldCrownModel",
        "io.redspace.ironsspellbooks.entity.armor.InfernalSorcererArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.PaladinArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.PlaguedArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.PyromancerArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.ShadowwalkerArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.TarnishedCrownModel",
        "io.redspace.ironsspellbooks.entity.armor.WanderingMagicianModel",
        "io.redspace.ironsspellbooks.entity.armor.netherite.NetheriteMageArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.priest.PriestArmorModel",
        "io.redspace.ironsspellbooks.entity.armor.pumpkin.PumpkinArmorModel"
}, remap = false)
public abstract class MixinIsbArmorModelLocations {

    @Redirect(
            method = {"getModelResource", "getTextureResource", "getAnimationResource"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private ResourceLocation coo$reuseLocation(String namespace, String path) {
        return CoOConfig.ironsspellbooksReuseArmorModelLocations
                ? ConstantLocations.of(namespace, path)
                : ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
