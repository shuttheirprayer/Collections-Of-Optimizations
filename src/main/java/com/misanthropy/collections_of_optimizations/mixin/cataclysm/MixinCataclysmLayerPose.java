package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.client.model.entity.Ignis_Model;
import com.github.L_Ender.cataclysm.client.model.entity.Ignited_Revenant_Model;
import com.github.L_Ender.cataclysm.client.render.layer.Ignis_Shield_Layer;
import com.github.L_Ender.cataclysm.client.render.layer.Revenant_Layer;
import com.github.L_Ender.cataclysm.client.render.layer.The_Harbinger_Shield_Layer;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignited_Revenant_Entity;
import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedEntityModel;
import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;

@Mixin(value = {Ignis_Shield_Layer.class, Revenant_Layer.class, The_Harbinger_Shield_Layer.class}, remap = false)
public abstract class MixinCataclysmLayerPose {

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/client/model/entity/Ignis_Model;setupAnim(Lcom/github/L_Ender/cataclysm/entity/AnimationMonster/BossMonsters/Ignis_Entity;FFFFF)V"),
            require = 0
    )
    private void coo$copyIgnisPose(Ignis_Model model, Ignis_Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                                   float netHeadYaw, float headPitch, Operation<Void> original) {
        if (!CoOConfig.cataclysmReuseLayerPose || !coo$copyPose(this.coo$parent(), model)) {
            original.call(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/client/model/entity/Ignited_Revenant_Model;setupAnim(Lcom/github/L_Ender/cataclysm/entity/AnimationMonster/BossMonsters/Ignited_Revenant_Entity;FFFFF)V"),
            require = 0
    )
    private void coo$copyRevenantPose(Ignited_Revenant_Model model, Ignited_Revenant_Entity entity, float limbSwing, float limbSwingAmount,
                                      float ageInTicks, float netHeadYaw, float headPitch, Operation<Void> original) {
        if (!CoOConfig.cataclysmReuseLayerPose || !coo$copyPose(this.coo$parent(), model)) {
            original.call(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;m_6973_(Lnet/minecraft/world/entity/Entity;FFFFF)V"),
            require = 0
    )
    private void coo$skipHarbingerRerun(EntityModel<Entity> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                                        float netHeadYaw, float headPitch, Operation<Void> original) {
        if (!CoOConfig.cataclysmReuseLayerPose || model != this.coo$parent()) {
            original.call(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Unique
    private EntityModel<?> coo$parent() {
        return ((RenderLayer<?, ?>) (Object) this).getParentModel();
    }

    @Unique
    private static boolean coo$copyPose(EntityModel<?> parent, AdvancedEntityModel<?> layerModel) {
        if (parent == null || parent.getClass() != layerModel.getClass()) {
            return false;
        }
        Iterator<AdvancedModelBox> from = ((AdvancedEntityModel<?>) parent).getAllParts().iterator();
        Iterator<AdvancedModelBox> to = layerModel.getAllParts().iterator();
        while (from.hasNext() && to.hasNext()) {
            AdvancedModelBox source = from.next();
            AdvancedModelBox target = to.next();
            target.rotateAngleX = source.rotateAngleX;
            target.rotateAngleY = source.rotateAngleY;
            target.rotateAngleZ = source.rotateAngleZ;
            target.rotationPointX = source.rotationPointX;
            target.rotationPointY = source.rotationPointY;
            target.rotationPointZ = source.rotationPointZ;
            target.xScale = source.xScale;
            target.yScale = source.yScale;
            target.zScale = source.zScale;
            target.showModel = source.showModel;
        }
        return true;
    }
}
