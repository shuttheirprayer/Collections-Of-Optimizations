package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.client.model.entity.Ancient_Remnant_Rework_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Aptrgangr_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Ceraunus_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Cindaria_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Clawdian_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Draugr_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Elemental_Spear_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Elite_Draugr_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Hippocamtus_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Ignited_Berserker_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Kobolediator_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Maledictus_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Netherite_Ministrosity_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Netherite_Monstrosity_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Royal_Draugr_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Scylla_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Storm_Serpent_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Symbiocto_Model;
import com.github.L_Ender.cataclysm.client.model.entity.The_Prowler_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Urchinkin_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Wave_Model;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(value = {
        Ancient_Remnant_Rework_Model.class, Aptrgangr_Model.class, Ceraunus_Model.class, Cindaria_Model.class, Clawdian_Model.class,
        Draugr_Model.class, Elemental_Spear_Model.class, Elite_Draugr_Model.class, Hippocamtus_Model.class, Ignited_Berserker_Model.class,
        Kobolediator_Model.class, Maledictus_Model.class, Netherite_Ministrosity_Model.class, Netherite_Monstrosity_Model.class,
        Royal_Draugr_Model.class, Scylla_Model.class, Storm_Serpent_Model.class, Symbiocto_Model.class, The_Prowler_Model.class,
        Urchinkin_Model.class, Wave_Model.class
}, remap = false)
public abstract class MixinCataclysmPoseReset {

    @WrapOperation(
            method = {"setupAnim", "m_6973_"},
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V", ordinal = 0),
            require = 0
    )
    private void coo$walkReset(Stream<?> parts, Consumer<?> action, Operation<Void> original) {
        if (!CoOConfig.cataclysmLeanPoseReset) {
            original.call(parts, action);
            return;
        }
        coo$reset(((HierarchicalModel<?>) (Object) this).root());
    }

    @Unique
    private static void coo$reset(ModelPart part) {
        part.resetPose();
        for (ModelPart child : ((CataclysmModelPartAccessor) (Object) part).coo$children().values()) {
            coo$reset(child);
        }
    }
}
