package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import net.minecraftforge.fml.loading.LoadingModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class DynamicLightColorHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String ARS = "com.hollingsworth.arsnouveau.common.mixin.light.LevelRendererMixin";
    private static final String HEXEREI = "net.joefoxe.hexerei.mixin.light.HexereiLevelRendererMixin";

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        String replacement;
        if (ARS.equals(mixinClassName) && isModPresent("ars_nouveau")) {
            replacement = "arsnouveau.MixinArsDynamicLightColor";
        } else if (HEXEREI.equals(mixinClassName) && isModPresent("hexerei")) {
            replacement = "hexerei.MixinHexereiDynamicLightColor";
        } else {
            return false;
        }
        LOGGER.info("Replacing {} with {}", mixinClassName, replacement);
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
