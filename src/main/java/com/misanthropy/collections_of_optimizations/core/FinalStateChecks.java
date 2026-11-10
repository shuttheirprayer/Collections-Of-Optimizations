package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FinalStateChecks {

    private static final Set<String> VALID = ConcurrentHashMap.newKeySet();

    private FinalStateChecks() {
    }

    public static void check(String state) {
        boolean once = CoOConfig.structureessentialsCheckFinalStateOnce;
        if (once && VALID.contains(state)) {
            return;
        }
        try {
            BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), state, true);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        if (once && VALID.size() < 65536) {
            VALID.add(state);
        }
    }
}
