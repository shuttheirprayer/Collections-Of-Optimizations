package com.misanthropy.collections_of_optimizations.mixin.lithostitched;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LithostitchedShuffleCache;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import dev.worldgen.lithostitched.worldgen.structure.LithostitchedTemplates;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(value = LithostitchedTemplates.class, remap = false)
public abstract class MixinLithostitchedTemplates {

    @Shadow
    @Final
    protected List<LithostitchedTemplates.WeightedEntry> entries;

    @Unique
    private LithostitchedShuffleCache coo$cache;

    @Inject(method = "shuffle", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$fastShuffle(RandomSource random, CallbackInfoReturnable<List<StructurePoolElement>> cir) {
        if (!CoOConfig.lithostitchedFastTemplateShuffle) {
            return;
        }

        int size = this.entries.size();
        if (size == 0) {
            cir.setReturnValue(List.of());
            return;
        }

        LithostitchedShuffleCache cache = this.coo$cache;
        if (cache == null || cache.elements.length != size) {
            cache = this.coo$buildCache(size);
            this.coo$cache = cache;
        }

        double[] keys = new double[size];
        int[] order = new int[size];
        for (int i = 0; i < size; i++) {
            keys[i] = -Math.pow(random.nextFloat(), cache.exponent[i]) + cache.bias[i];
            order[i] = i;
        }

        coo$sortStable(order, keys, size);

        StructurePoolElement[] shuffled = new StructurePoolElement[size];
        for (int i = 0; i < size; i++) {
            shuffled[i] = cache.elements[order[i]];
        }
        cir.setReturnValue(Arrays.asList(shuffled));
    }

    @Unique
    private LithostitchedShuffleCache coo$buildCache(int size) {
        StructurePoolElement[] elements = new StructurePoolElement[size];
        double[] exponent = new double[size];
        double[] bias = new double[size];
        for (int i = 0; i < size; i++) {
            LithostitchedTemplates.WeightedEntry entry = this.entries.get(i);
            StructurePoolElement element = entry.getElement();
            elements[i] = element;
            exponent[i] = 1.0F / (float) ((LithostitchedWeightedEntryAccessor) entry).coo$weight();
            bias[i] = element instanceof DelegatingPoolElement delegating && delegating.prioritized() ? -2 : 0;
        }
        return new LithostitchedShuffleCache(elements, exponent, bias);
    }

    @Unique
    private static void coo$sortStable(int[] order, double[] keys, int size) {
        if (size > 64) {
            IntArrays.stableSort(order, (left, right) -> Double.compare(keys[left], keys[right]));
            return;
        }
        for (int i = 1; i < size; i++) {
            int current = order[i];
            double key = keys[current];
            int low = 0;
            int high = i;
            while (low < high) {
                int mid = (low + high) >>> 1;
                if (Double.compare(keys[order[mid]], key) <= 0) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            System.arraycopy(order, low, order, low + 1, i - low);
            order[low] = current;
        }
    }
}
