package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class CbcEntityHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String CANCELLED = "rbasamoyai.createbigcannons.mixin.EntityMixin";
    private static final String PINNED_VERSION = "5.11.4";

    private static boolean announced;

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.equals(mixinClassName) || !PINNED_VERSION.equals(cbcVersion())) {
            return false;
        }
        if (!announced) {
            announced = true;
            LOGGER.info("Replacing {} with cbc.MixinCbcAutocannonSeat", CANCELLED);
        }
        return true;
    }

    private static String cbcVersion() {
        try {
            ModFileInfo modFile = LoadingModList.get().getModFileById("createbigcannons");
            if (modFile != null) {
                for (IModInfo mod : modFile.getMods()) {
                    if (mod.getModId().equals("createbigcannons")) {
                        return mod.getVersion().toString();
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return "";
    }
}
