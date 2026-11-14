package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WarmInit {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final String[][] CLASSES = {
            {"supplementaries", "net.mehvahdjukaar.supplementaries.client.TextUtils"}
    };
    private static boolean done;

    private WarmInit() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(WarmInit::onClientTick);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (done || event.phase != TickEvent.Phase.END || !CoOConfig.vanillaWarmHeavyClassInit) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.getOverlay() != null || mc.font == null) {
            return;
        }
        done = true;
        long begin = System.nanoTime();
        int count = 0;
        for (String[] entry : CLASSES) {
            if (!ModList.get().isLoaded(entry[0])) {
                continue;
            }
            try {
                Class.forName(entry[1], true, WarmInit.class.getClassLoader());
                count++;
            } catch (Throwable t) {
                LOGGER.warn("Could not initialise {} early", entry[1], t);
            }
        }
        LOGGER.info("Initialised {} heavy client classes early in {} ms", count, (System.nanoTime() - begin) / 1_000_000L);
    }
}
