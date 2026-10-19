package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.StaticHardwareReport;
import net.minecraft.SystemReport;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(SystemReport.class)
public abstract class MixinSystemReportStaticHardware {

    @Shadow
    @Final
    private Map<String, String> entries;

    @WrapMethod(method = "ignoreErrors(Ljava/lang/String;Ljava/lang/Runnable;)V", require = 0)
    private void coo$reuseStaticHardwareLines(String group, Runnable task, Operation<Void> original) {
        if (!CoOConfig.vanillaCacheStaticHardwareReport || !StaticHardwareReport.isStatic(group)) {
            original.call(group, task);
            return;
        }
        Map<String, String> known = StaticHardwareReport.lines(group);
        if (known != null) {
            this.entries.putAll(known);
            return;
        }
        Map<String, String> before = Map.copyOf(this.entries);
        original.call(group, task);
        StaticHardwareReport.remember(group, before, this.entries);
    }
}
