package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.google.common.collect.BiMap;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.RegistrySnapshotReuse;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class MixinForgeRegistrySnapshotReuse<V> {

    @Shadow
    @Final
    private BiMap<Integer, V> ids;
    @Shadow
    @Final
    private Map<ResourceLocation, ResourceLocation> aliases;
    @Shadow
    @Final
    private IntSet blocked;

    @Shadow
    abstract Map<ResourceLocation, String> getOverrideOwners();

    @Shadow
    public abstract ResourceLocation getKey(V value);

    @Unique
    private ForgeRegistry.Snapshot coo$snapshot;
    @Unique
    private int[] coo$ids;
    @Unique
    private Object[] coo$values;
    @Unique
    private ResourceLocation[] coo$keys;
    @Unique
    private Map<ResourceLocation, ResourceLocation> coo$aliases;
    @Unique
    private IntSet coo$blocked;
    @Unique
    private Map<ResourceLocation, String> coo$overrides;

    @Inject(method = "makeSnapshot", at = @At("HEAD"), cancellable = true)
    private void coo$reuseSavedSnapshot(CallbackInfoReturnable<ForgeRegistry.Snapshot> cir) {
        if (CoOConfig.forgeReuseRegistrySnapshots && RegistrySnapshotReuse.saving() && coo$unchanged()) {
            cir.setReturnValue(coo$snapshot);
        }
    }

    @Inject(method = "makeSnapshot", at = @At("RETURN"))
    private void coo$rememberSavedSnapshot(CallbackInfoReturnable<ForgeRegistry.Snapshot> cir) {
        if (!CoOConfig.forgeReuseRegistrySnapshots || !RegistrySnapshotReuse.saving() || cir.getReturnValue() == coo$snapshot) {
            return;
        }
        int size = ids.size();
        int[] idArray = new int[size];
        Object[] valueArray = new Object[size];
        ResourceLocation[] keyArray = new ResourceLocation[size];
        int i = 0;
        for (Map.Entry<Integer, V> entry : ids.entrySet()) {
            idArray[i] = entry.getKey();
            valueArray[i] = entry.getValue();
            keyArray[i] = getKey(entry.getValue());
            i++;
        }
        coo$ids = idArray;
        coo$values = valueArray;
        coo$keys = keyArray;
        coo$aliases = new HashMap<>(aliases);
        coo$blocked = new IntOpenHashSet(blocked);
        coo$overrides = getOverrideOwners();
        coo$snapshot = cir.getReturnValue();
    }

    @Unique
    private boolean coo$unchanged() {
        ForgeRegistry.Snapshot snapshot = coo$snapshot;
        int[] idArray = coo$ids;
        if (snapshot == null || idArray.length != ids.size()) {
            return false;
        }
        Object[] valueArray = coo$values;
        ResourceLocation[] keyArray = coo$keys;
        int i = 0;
        for (Map.Entry<Integer, V> entry : ids.entrySet()) {
            V value = entry.getValue();
            if (entry.getKey() != idArray[i] || value != valueArray[i] || !keyArray[i].equals(getKey(value))) {
                return false;
            }
            i++;
        }
        return aliases.equals(coo$aliases) && blocked.equals(coo$blocked) && getOverrideOwners().equals(coo$overrides);
    }
}
