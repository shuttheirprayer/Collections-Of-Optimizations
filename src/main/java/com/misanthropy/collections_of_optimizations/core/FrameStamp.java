package com.misanthropy.collections_of_optimizations.core;

public final class FrameStamp {

    public static int frame = 1;

    private FrameStamp() {
    }

    public interface TransmogMemo {
        int coo$transmogFrame();

        boolean coo$transmogged();

        void coo$transmogMemo(int frame, boolean transmogged);
    }
}
