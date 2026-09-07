package com.misanthropy.collections_of_optimizations.mixin.cucumber;

import com.blakebr0.cucumber.crafting.TagMapper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = TagMapper.class, remap = false)
public class MixinCucumberTagMapper {

    @Unique
    private static final Map<String, Optional<Item>> coo$resolved = new ConcurrentHashMap<>();

    @WrapMethod(method = "getItemForTag")
    private static Item coo$memoiseTagLookup(String tag, Operation<Item> original) {
        if (!CoOConfig.cucumberCacheTagLookup) {
            return original.call(tag);
        }

        Optional<Item> cached = coo$resolved.get(tag);
        if (cached != null) {
            return cached.orElse(null);
        }

        Item item = original.call(tag);
        coo$resolved.put(tag, Optional.ofNullable(item));
        return item;
    }

    @Inject(method = "reloadTagMappings", at = @At("HEAD"), require = 0)
    private static void coo$dropResolved(CallbackInfo ci) {
        coo$resolved.clear();
    }
}
