package com.misanthropy.collections_of_optimizations.mixin.alltheleaks;

import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.uncandango.alltheleaks.config.ATLProperties;
import dev.uncandango.alltheleaks.feature.common.mods.minecraft.MemoryMonitor;
import dev.uncandango.alltheleaks.report.ReportManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ReportManager.class, remap = false)
public abstract class MixinAtlLazyLeakSummary {

    @ModifyVariable(method = "registerTask", at = @At("HEAD"), argsOnly = true)
    private static Runnable coo$lazyLeakSummary(Runnable task, @Local(argsOnly = true) String name) {
        if (!CoOConfig.alltheleaksLazyLeakSummary) {
            return task;
        }
        if ("update_leak_summary".equals(name)) {
            return () -> {
                if (ATLProperties.get().showSummaryOnDebugScreen) {
                    task.run();
                }
            };
        }
        if ("passive_memory_leak_report".equals(name)) {
            return () -> {
                MemoryMonitor.updateLeakSummary();
                task.run();
            };
        }
        return task;
    }
}
