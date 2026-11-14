package com.misanthropy.collections_of_optimizations.core;

import com.simibubi.create.content.contraptions.render.ClientContraption;
import dev.engine_room.flywheel.lib.util.LevelAttached;

public final class ContraptionRenderLevelRelease {

    private ContraptionRenderLevelRelease() {
    }

    public static void release(Object clientContraption) {
        LevelAttached.invalidateLevel(((ClientContraption) clientContraption).getRenderLevel());
    }
}
