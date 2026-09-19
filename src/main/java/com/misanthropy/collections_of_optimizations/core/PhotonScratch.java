package com.misanthropy.collections_of_optimizations.core;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PhotonScratch {

    private static final ThreadLocal<PhotonScratch> LOCAL =
            new RenderThreadLocal<>(ThreadLocal.withInitial(PhotonScratch::new));

    public final Vector3f[] corners = {new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};

    public final Vector3f position = new Vector3f();

    public final Vector3f lightPosition = new Vector3f();

    public final Quaternionf rotation = new Quaternionf();

    private PhotonScratch() {
    }

    public static PhotonScratch get() {
        return LOCAL.get();
    }
}
