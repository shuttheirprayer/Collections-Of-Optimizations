package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import net.minecraftforge.fml.loading.LoadingModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class CableFacadesStateHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String CANCELLED = "com.portingdeadmods.cable_facades.mixins.BlockStateBaseMixin";

    private static boolean announced;

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.equals(mixinClassName) || !isModPresent("cable_facades")) {
            return false;
        }
        if (!announced) {
            announced = true;
            LOGGER.info("Replacing {} with cablefacades.MixinCableFacadesLeanStateHooks", CANCELLED);
        }
        return true;
    }

    private static boolean isModPresent(String modId) {
        try {
            return LoadingModList.get().getModFileById(modId) != null;
        } catch (Throwable throwable) {
            return false;
        }
    }
}
