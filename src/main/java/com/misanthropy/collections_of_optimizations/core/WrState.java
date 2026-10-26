package com.misanthropy.collections_of_optimizations.core;

import com.alexander.witherreincarnated.capabilities.possessed.PossessedCapability;
import com.alexander.witherreincarnated.capabilities.possessed.PossessedCapabilityProvider;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public final class WrState {

    private static int serverWithers;
    private static int clientWithers;

    private WrState() {
    }

    public static void witherAdded(boolean clientSide) {
        if (clientSide) {
            clientWithers++;
        } else {
            serverWithers++;
        }
    }

    public static void witherRemoved(boolean clientSide) {
        if (clientSide) {
            clientWithers = Math.max(0, clientWithers - 1);
        } else {
            serverWithers = Math.max(0, serverWithers - 1);
        }
    }

    public static boolean noServerWithers() {
        return CoOConfig.wrSkipIdleWitherScans && serverWithers == 0;
    }

    public static boolean noClientWithers() {
        return CoOConfig.wrSkipIdleWitherScans && clientWithers == 0;
    }

    public static PossessedCapability cap(Entity entity) {
        return entity instanceof Mob ? ((WrCapCache) entity).coo$wrCap() : null;
    }

    public static boolean isPossessed(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!CoOConfig.wrFastPossessionChecks) {
            return entity.getCapability(PossessedCapabilityProvider.CAP).map(PossessedCapability::isPossessed).orElse(false);
        }
        PossessedCapability cap = cap(entity);
        return cap != null && cap.possessionTime > 0;
    }

    public static boolean isIdle(PossessedCapability cap) {
        return cap.possessionTime <= 0
                && cap.possessedFor == 0
                && cap.possessionCooldown == 0
                && !cap.wasPossessed
                && cap.possessedBy == null
                && cap.possessedByUUID == null;
    }
}
