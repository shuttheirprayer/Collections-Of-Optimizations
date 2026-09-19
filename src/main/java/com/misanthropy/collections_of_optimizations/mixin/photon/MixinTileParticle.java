package com.misanthropy.collections_of_optimizations.mixin.photon;

import com.lowdragmc.photon.client.gameobject.emitter.IParticleEmitter;
import com.lowdragmc.photon.client.gameobject.emitter.data.RendererSetting;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleConfig;
import com.lowdragmc.photon.client.gameobject.particle.TileParticle;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PhotonScratch;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = TileParticle.class, remap = false)
public abstract class MixinTileParticle {

    @Unique
    private BlockPos coo$lastLightPos;

    @Shadow
    protected ParticleConfig config;

    @Shadow
    protected IParticleEmitter emitter;

    @Shadow
    protected float localX;
    @Shadow
    protected float localY;
    @Shadow
    protected float localZ;
    @Shadow
    protected float localXo;
    @Shadow
    protected float localYo;
    @Shadow
    protected float localZo;

    @Shadow
    protected float rotationX;
    @Shadow
    protected float rotationY;
    @Shadow
    protected float rotationZ;
    @Shadow
    protected float rotationXo;
    @Shadow
    protected float rotationYo;
    @Shadow
    protected float rotationZo;

    @Shadow
    protected float sizeX;
    @Shadow
    protected float sizeY;
    @Shadow
    protected float sizeZ;
    @Shadow
    protected float sizeXo;
    @Shadow
    protected float sizeYo;
    @Shadow
    protected float sizeZo;

    @Shadow
    protected boolean isRemoved;

    @Shadow
    public abstract Matrix4f getSpaceTransform();

    @Shadow
    public abstract Vector3f getSpaceScale();

    @Shadow
    public abstract Vector4f getRealColor(float partialTicks);

    @Shadow
    public abstract int getRealLight(float partialTicks);

    @WrapMethod(method = "getLightColor", require = 0)
    private int coo$leanLightLookup(Operation<Integer> original) {
        if (!CoOConfig.photonLeanParticleLight) {
            return original.call();
        }
        Vector3f pos = PhotonScratch.get().lightPosition;
        if (this.isRemoved) {
            pos.set(this.localX, this.localY, this.localZ);
        } else {
            pos.set(this.localXo, this.localYo, this.localZo);
            if (this.config.noise.isEnable()) {
                pos.add(this.config.noise.getPosition((TileParticle) (Object) this, 0));
            }
        }
        pos.mulPosition(getSpaceTransform());

        int blockX = (int) pos.x;
        int blockY = (int) pos.y;
        int blockZ = (int) pos.z;
        BlockPos blockPos = this.coo$lastLightPos;
        if (blockPos == null || blockPos.getX() != blockX || blockPos.getY() != blockY || blockPos.getZ() != blockZ) {
            blockPos = new BlockPos(blockX, blockY, blockZ);
            this.coo$lastLightPos = blockPos;
        }
        return this.emitter.getLightColor(blockPos);
    }

    @WrapMethod(method = "renderInternal", require = 0)
    private void coo$leanBillboardVertices(VertexConsumer buffer, Camera camera, float partialTicks,
                                           Operation<Void> original) {
        if (!CoOConfig.photonLeanParticleQuads) {
            original.call(buffer, camera, partialTicks);
            return;
        }
        RendererSetting.Particle.Mode renderMode = this.config.renderer.getRenderMode();
        if (renderMode == RendererSetting.Particle.Mode.Model) {
            original.call(buffer, camera, partialTicks);
            return;
        }

        TileParticle self = (TileParticle) (Object) this;
        PhotonScratch scratch = PhotonScratch.get();
        Vector3f[] corners = scratch.corners;

        Vector3f localPos = scratch.position;
        if (this.isRemoved) {
            localPos.set(this.localX, this.localY, this.localZ);
        } else {
            localPos.set(Mth.lerp(partialTicks, this.localXo, this.localX),
                    Mth.lerp(partialTicks, this.localYo, this.localY),
                    Mth.lerp(partialTicks, this.localZo, this.localZ));
            if (this.config.noise.isEnable()) {
                localPos.add(this.config.noise.getPosition(self, partialTicks));
            }
        }
        localPos.mulPosition(getSpaceTransform());

        var vec3 = camera.getPosition();
        float x = (float) (localPos.x - vec3.x);
        float y = (float) (localPos.y - vec3.y);
        float z = (float) (localPos.z - vec3.z);

        Vector4f color = getRealColor(partialTicks);
        float r = color.x();
        float g = color.y();
        float b = color.z();
        float a = color.w();

        int light = getRealLight(partialTicks);

        float rotX = Mth.lerp(partialTicks, this.rotationXo, this.rotationX);
        float rotY = Mth.lerp(partialTicks, this.rotationYo, this.rotationY);
        float rotZ = Mth.lerp(partialTicks, this.rotationZo, this.rotationZ);

        Quaternionf quaternion = renderMode.quaternion.apply(self, camera, partialTicks);
        if (rotX != 0 || rotY != 0 || rotZ != 0) {
            quaternion = scratch.rotation.set(quaternion).rotateXYZ(rotX, rotY, rotZ);
        }

        float sX = Mth.lerp(partialTicks, this.sizeXo, this.sizeX);
        float sY = Mth.lerp(partialTicks, this.sizeYo, this.sizeY);
        float sZ = Mth.lerp(partialTicks, this.sizeZo, this.sizeZ);

        Vector3f spaceScale = getSpaceScale();

        corners[0].set(-1.0F, -1.0F, 0.0F);
        corners[1].set(-1.0F, 1.0F, 0.0F);
        corners[2].set(1.0F, 1.0F, 0.0F);
        corners[3].set(1.0F, -1.0F, 0.0F);
        for (int i = 0; i < 4; ++i) {
            Vector3f vertex = corners[i];
            vertex.mul(sX, sY, sZ);
            quaternion.transform(vertex);
            vertex.mul(spaceScale);
            vertex.add(x, y, z);
        }

        float u0 = 0.0F;
        float v0 = 0.0F;
        float u1 = 1.0F;
        float v1 = 1.0F;
        if (this.config.uvAnimation.isEnable()) {
            Vector4f uvs = this.config.uvAnimation.getUVs(self, partialTicks);
            u0 = uvs.x();
            v0 = uvs.y();
            u1 = uvs.z();
            v1 = uvs.w();
        }

        buffer.vertex(corners[0].x(), corners[0].y(), corners[0].z()).uv(u1, v1).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(corners[1].x(), corners[1].y(), corners[1].z()).uv(u1, v0).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(corners[2].x(), corners[2].y(), corners[2].z()).uv(u0, v0).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(corners[3].x(), corners[3].y(), corners[3].z()).uv(u0, v1).color(r, g, b, a).uv2(light).endVertex();
    }
}
