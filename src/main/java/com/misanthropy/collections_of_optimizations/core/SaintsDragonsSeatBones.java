package com.misanthropy.collections_of_optimizations.core;

import com.leon.saintsdragons.client.renderer.RiderConfig;

public final class SaintsDragonsSeatBones {

    private static RiderConfig.RiderSpec lastSpec;
    private static String[] lastBones = new String[0];

    private SaintsDragonsSeatBones() {
    }

    public static boolean isSeatBone(Object dragon, String boneName) {
        RiderConfig.RiderSpec spec = RiderConfig.getSpec(dragon);
        if (spec == null) {
            return false;
        }
        String[] bones = lastBones;
        if (spec != lastSpec) {
            bones = spec.seats().values().stream().map(RiderConfig.SeatSpec::boneName).toArray(String[]::new);
            lastBones = bones;
            lastSpec = spec;
        }
        for (String bone : bones) {
            if (bone.equals(boneName)) {
                return true;
            }
        }
        return false;
    }
}
