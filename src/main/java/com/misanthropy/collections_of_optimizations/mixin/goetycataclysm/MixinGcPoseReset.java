package com.misanthropy.collections_of_optimizations.mixin.goetycataclysm;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Pseudo
@Mixin(targets = {
        "com.Polarice3.goety_cataclysm.client.render.model.AncientRemnantServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.AptrgangrServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.CindariaServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.ClawdianServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.DraugrNecromancerModel",
        "com.Polarice3.goety_cataclysm.client.render.model.DraugrServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.EliteDraugrServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.FlareBombModel",
        "com.Polarice3.goety_cataclysm.client.render.model.HippocamtusServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.IgnitedBerserkerServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.KobolediatorServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.NMServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.ProwlerServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.RoyalDraugrServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.StormSerpentModel",
        "com.Polarice3.goety_cataclysm.client.render.model.SymbioctoServantModel",
        "com.Polarice3.goety_cataclysm.client.render.model.UrchinkinServantModel"
}, remap = false)
public abstract class MixinGcPoseReset {

    @WrapOperation(
            method = {"setupAnim", "m_6973_"},
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V", ordinal = 0),
            require = 0
    )
    private void coo$walkReset(Stream<?> parts, Consumer<?> action, Operation<Void> original) {
        if (!CoOConfig.goetycataclysmLeanPoseReset) {
            original.call(parts, action);
            return;
        }
        coo$reset(((HierarchicalModel<?>) (Object) this).root());
    }

    @Unique
    private static void coo$reset(ModelPart part) {
        part.resetPose();
        for (ModelPart child : ((GcModelPartAccessor) (Object) part).coo$gcChildren().values()) {
            coo$reset(child);
        }
    }
}
