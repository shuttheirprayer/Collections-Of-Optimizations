package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.flywheel.FlwIndirectProgramsAccessor;
import com.misanthropy.collections_of_optimizations.mixin.flywheel.FlwInstancingProgramsAccessor;
import com.misanthropy.collections_of_optimizations.mixin.flywheel.FlwPipelineCompilerAccessor;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.backend.compile.ContextShader;
import dev.engine_room.flywheel.backend.compile.IndirectPrograms;
import dev.engine_room.flywheel.backend.compile.InstancingPrograms;
import dev.engine_room.flywheel.backend.compile.PipelineCompiler;
import dev.engine_room.flywheel.backend.compile.core.CompilationHarness;
import dev.engine_room.flywheel.lib.material.SimpleLightShader;
import dev.engine_room.flywheel.lib.material.SimpleMaterialShaders;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FlywheelWarm {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final int MAX_LINES = 400;
    private static final int MAX_TRIES = 600;

    private static final Map<String, InstanceType<?>> TYPES = new ConcurrentHashMap<>();
    private static final Map<String, String> ORIGINS = new ConcurrentHashMap<>();
    private static final Set<String> KNOWN = ConcurrentHashMap.newKeySet();
    private static final ArrayDeque<String> PENDING = new ArrayDeque<>();
    private static boolean loaded;
    private static boolean dirty;
    private static int tries;
    private static int warmed;
    private static int utilStep;
    private static long warmNanos;

    private FlywheelWarm() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(FlywheelWarm::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(FlywheelWarm::onScreenOpening);
        MinecraftForge.EVENT_BUS.addListener(FlywheelWarm::onShutdown);
    }

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("collections_of_optimizations").resolve("warm-flywheel.txt");
    }

    private static String typeKey(InstanceType<?> type) {
        return type.vertexShader() + " " + type.cullShader();
    }

    public static void registerType(InstanceType<?> type) {
        try {
            String key = typeKey(type);
            TYPES.putIfAbsent(key, type);
            StackWalker.getInstance().walk(frames -> frames
                    .filter(frame -> frame.getMethodName().equals("<clinit>"))
                    .findFirst())
                    .ifPresent(frame -> ORIGINS.putIfAbsent(key, frame.getClassName()));
        } catch (Throwable ignored) {
        }
    }

    private static void initOrigin(String origin) {
        try {
            Class.forName(origin, true, FlywheelWarm.class.getClassLoader());
        } catch (Throwable ignored) {
        }
    }

    public static void compiled(Object harness, Object key) {
        if (!CoOConfig.flywheelWarmPrograms) {
            return;
        }
        try {
            String line = describe(harness, key);
            if (line != null && KNOWN.size() < MAX_LINES && KNOWN.add(line)) {
                dirty = true;
            }
        } catch (Throwable ignored) {
        }
    }

    private static String role(Object harness) {
        IndirectPrograms indirect = IndirectPrograms.get();
        if (indirect != null) {
            FlwIndirectProgramsAccessor access = (FlwIndirectProgramsAccessor) (Object) indirect;
            if (harness == ((FlwPipelineCompilerAccessor) (Object) access.coo$pipeline()).coo$harness()) {
                return "indirect";
            }
            if (harness == access.coo$culling()) {
                return "cull";
            }
        }
        InstancingPrograms instancing = InstancingPrograms.get();
        if (instancing != null) {
            PipelineCompiler pipeline = ((FlwInstancingProgramsAccessor) (Object) instancing).coo$pipeline();
            if (harness == ((FlwPipelineCompilerAccessor) (Object) pipeline).coo$harness()) {
                return "instancing";
            }
        }
        return null;
    }

    private static String describe(Object harness, Object key) {
        String role = role(harness);
        if (role == null) {
            return null;
        }
        if (key instanceof PipelineCompiler.PipelineProgramKey k && !role.equals("cull")) {
            if (k.debugEnabled() || !(k.light() instanceof SimpleLightShader light) || !(k.materialShaders() instanceof SimpleMaterialShaders shaders)) {
                return null;
            }
            return role + "|" + typeKey(k.instanceType()) + "|" + k.contextShader().name() + "|" + light.source() + "|"
                    + shaders.vertexSource() + "|" + shaders.fragmentSource() + "|" + k.useCutout() + "|" + k.oit().name()
                    + "|" + ORIGINS.getOrDefault(typeKey(k.instanceType()), "-");
        }
        if (key instanceof InstanceType<?> type && role.equals("cull")) {
            return role + "|" + typeKey(type) + "|" + ORIGINS.getOrDefault(typeKey(type), "-");
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean warm(String line) {
        String[] p = line.split("\\|");
        InstanceType<?> type = p.length > 1 ? TYPES.get(p[1]) : null;
        if (type == null && p.length > 2 && !p[p.length - 1].equals("-")) {
            initOrigin(p[p.length - 1]);
            type = TYPES.get(p[1]);
        }
        if (type == null) {
            return false;
        }
        if (p[0].equals("cull") && p.length == 3) {
            IndirectPrograms indirect = IndirectPrograms.get();
            if (indirect == null) {
                return false;
            }
            ((CompilationHarness) ((FlwIndirectProgramsAccessor) (Object) indirect).coo$culling()).get(type);
            return true;
        }
        if (p.length != 9) {
            return true;
        }
        PipelineCompiler pipeline = null;
        if (p[0].equals("indirect") && IndirectPrograms.get() != null) {
            pipeline = ((FlwIndirectProgramsAccessor) (Object) IndirectPrograms.get()).coo$pipeline();
        } else if (p[0].equals("instancing") && InstancingPrograms.get() != null) {
            pipeline = ((FlwInstancingProgramsAccessor) (Object) InstancingPrograms.get()).coo$pipeline();
        }
        if (pipeline == null) {
            return false;
        }
        PipelineCompiler.PipelineProgramKey key = new PipelineCompiler.PipelineProgramKey(type, ContextShader.valueOf(p[2]),
                new SimpleLightShader(new ResourceLocation(p[3])),
                new SimpleMaterialShaders(new ResourceLocation(p[4]), new ResourceLocation(p[5])),
                Boolean.parseBoolean(p[6]), false, PipelineCompiler.OitMode.valueOf(p[7]));
        ((FlwPipelineCompilerAccessor) (Object) pipeline).coo$harness().get(key);
        return true;
    }

    private static void load() {
        loaded = true;
        try {
            Path path = file();
            if (Files.exists(path)) {
                for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                    if (!line.isBlank() && KNOWN.add(line.trim())) {
                        PENDING.add(line.trim());
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Flywheel program warmup list could not be read", t);
        }
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !CoOConfig.flywheelWarmPrograms) {
            return;
        }
        if (!loaded) {
            load();
        }
        if (PENDING.isEmpty()) {
            warmUtility();
            return;
        }
        String line = PENDING.peek();
        long begin = System.nanoTime();
        boolean done;
        try {
            done = warm(line);
        } catch (Throwable t) {
            done = true;
            KNOWN.remove(line);
            dirty = true;
        }
        if (done) {
            PENDING.poll();
            warmed++;
            warmNanos += System.nanoTime() - begin;
            if (PENDING.isEmpty()) {
                LOGGER.info("Flywheel warmup compiled {} programs in {} ms", warmed, warmNanos / 1_000_000L);
            }
        } else if (++tries > MAX_TRIES) {
            LOGGER.info("Flywheel warmup gave up on {} programs it could not resolve", PENDING.size());
            PENDING.clear();
        }
    }

    private static void warmUtility() {
        if (utilStep >= 6) {
            return;
        }
        IndirectPrograms indirect = IndirectPrograms.get();
        if (indirect == null) {
            return;
        }
        long begin = System.nanoTime();
        try {
            switch (utilStep) {
                case 0 -> indirect.getApplyProgram();
                case 1 -> indirect.getScatterProgram();
                case 2 -> indirect.getDownsampleFirstProgram();
                case 3 -> indirect.getDownsampleSecondProgram();
                case 4 -> indirect.oitPrograms().getOitCompositeProgram();
                default -> indirect.oitPrograms().getOitDepthProgram();
            }
        } catch (Throwable t) {
            utilStep = 6;
            return;
        }
        utilStep++;
        warmNanos += System.nanoTime() - begin;
        if (utilStep == 6) {
            LOGGER.info("Flywheel warmup compiled its 6 utility programs, {} ms in total", warmNanos / 1_000_000L);
        }
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen) {
            save();
        }
    }

    private static void onShutdown(GameShuttingDownEvent event) {
        save();
    }

    private static void save() {
        if (!loaded) {
            load();
        }
        if (!dirty) {
            return;
        }
        dirty = false;
        try {
            Path path = file();
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>(KNOWN);
            lines.sort(null);
            Path temp = path.resolveSibling("warm-flywheel.txt.tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable t) {
            LOGGER.warn("Flywheel program warmup list could not be saved", t);
        }
    }
}
