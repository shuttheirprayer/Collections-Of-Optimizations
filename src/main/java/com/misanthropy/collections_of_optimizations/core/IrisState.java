package com.misanthropy.collections_of_optimizations.core;

import net.irisshaders.iris.api.v0.IrisApi;

public final class IrisState {

    private static final IrisApi API = resolve();

    private IrisState() {
    }

    private static IrisApi resolve() {
        try {
            return IrisApi.getInstance();
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static boolean shadowPass() {
        if (API == null) {
            return false;
        }
        try {
            return API.isRenderingShadowPass();
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static boolean shaderPackInUse() {
        if (API == null) {
            return false;
        }
        try {
            return API.isShaderPackInUse();
        } catch (Throwable throwable) {
            return false;
        }
    }
}
