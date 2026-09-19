package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public final class MguChickenSwellModel {

    private static LayerDefinition layer;
    private static ModelPart root;

    private MguChickenSwellModel() {
    }

    public static LayerDefinition getLayer() {
        return layer;
    }

    public static void setLayer(LayerDefinition value) {
        layer = value;
    }

    public static ModelPart getRoot() {
        return root;
    }

    public static void setRoot(ModelPart value) {
        root = value;
    }
}
