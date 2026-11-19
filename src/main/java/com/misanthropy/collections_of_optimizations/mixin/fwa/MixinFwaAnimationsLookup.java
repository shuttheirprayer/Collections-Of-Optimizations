package com.misanthropy.collections_of_optimizations.mixin.fwa;

import fr.madu59.fwa.Animations;
import fr.madu59.fwa.anims.Animation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = Animations.class, remap = false)
public abstract class MixinFwaAnimationsLookup {

    @Shadow @Final
    public ConcurrentHashMap<Long, ConcurrentHashMap<BlockPos, Animation>> animations;

    @Unique
    private volatile byte[] coo$sections = new byte[16384];

    @Unique
    private int coo$marked;

    @Unique
    private static int coo$slot(long sectionKey) {
        long mixed = sectionKey * 0x9E3779B97F4A7C15L;
        return (int) (mixed >>> 50);
    }

    @Inject(method = "add", at = @At("HEAD"))
    private void coo$markSection(BlockPos pos, Animation animation, CallbackInfo ci) {
        synchronized (this.animations) {
            byte[] sections = this.coo$sections;
            int slot = coo$slot(SectionPos.asLong(pos));
            if (sections[slot] != 0) {
                return;
            }
            if (++this.coo$marked <= 4096) {
                sections[slot] = 1;
                return;
            }
            byte[] fresh = new byte[16384];
            int marked = 1;
            fresh[slot] = 1;
            for (Long key : this.animations.keySet()) {
                int other = coo$slot(key);
                if (fresh[other] == 0) {
                    fresh[other] = 1;
                    marked++;
                }
            }
            this.coo$marked = marked;
            this.coo$sections = fresh;
        }
    }

    @Overwrite
    public Animation getAt(BlockPos blockPos) {
        long key = SectionPos.asLong(blockPos);
        if (this.coo$sections[coo$slot(key)] == 0) {
            return null;
        }
        ConcurrentHashMap<BlockPos, Animation> section = this.animations.get(key);
        return section == null ? null : section.get(blockPos);
    }

    @Overwrite
    public boolean containsAt(BlockPos blockPos) {
        long key = SectionPos.asLong(blockPos);
        if (this.coo$sections[coo$slot(key)] == 0) {
            return false;
        }
        ConcurrentHashMap<BlockPos, Animation> section = this.animations.get(key);
        return section != null && section.containsKey(blockPos);
    }
}
