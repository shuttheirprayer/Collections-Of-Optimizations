package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.utils.EMFEntity;

public final class EmfStatuePause {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static BlockEntityType<?> statue;

    private EmfStatuePause() {
    }

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EmfStatuePause::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        statue = ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation("supplementaries", "statue"));
        if (statue == null) {
            return;
        }
        try {
            EMFAnimationApi.registerPauseCondition(EmfStatuePause::isStatue);
        } catch (Exception e) {
            LOGGER.warn("Could not pause EMF animations on statues", e);
        }
    }

    private static Boolean isStatue(EMFEntity entity) {
        return entity instanceof BlockEntity blockEntity && blockEntity.getType() == statue;
    }
}
