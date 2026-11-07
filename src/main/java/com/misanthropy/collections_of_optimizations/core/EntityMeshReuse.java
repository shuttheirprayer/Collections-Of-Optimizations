package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.logging.LogUtils;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.attributes.CommonVertexAttribute;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

public final class EntityMeshReuse {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Unsafe MEMORY = unsafe();
    private static final long BYTES = MEMORY == null ? 0L : MEMORY.arrayBaseOffset(byte[].class);

    public static boolean enabled = true;
    public static int poseHz;
    public static int maxInterval = 6;
    public static float smallPixels = -1F;
    public static int maxSmallInterval = 12;
    public static long replays;
    public static long records;

    private static final int CHUNK = 256;
    private static final int SLOTS = 12;
    private static final int M_POS = 1, M_COLOR = 2, M_UV = 4, M_OVERLAY = 8, M_LIGHT = 16, M_NORMAL = 32, M_BULK = 64;

    private static final Set<EntityType<?>> UNSAFE = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Set<BlockEntityType<?>> UNSAFE_BLOCK_ENTITIES = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Recorder RECORDER = new Recorder();
    private static final Matrix4f DELTA = new Matrix4f();
    private static final Matrix3f DELTA_NORMAL = new Matrix3f();

    private static int frame;
    private static long lastFrameNanos;
    private static double frameNanos = 1.0E9 / 60.0;
    private static boolean shaders;
    private static int interval = 1;
    private static int refreshRate = 240;
    private static int flushDepth;
    private static Cache recording;
    private static ByteBuffer scratch = ByteBuffer.allocateDirect(64 * 1024);

    private EntityMeshReuse() {
    }

    public interface Holder {
        Cache coo$meshCache();
    }

    public static void beginFrame() {
        long now = System.nanoTime();
        if (lastFrameNanos != 0L) {
            long spent = now - lastFrameNanos;
            if (spent > 0L && spent < 250_000_000L) {
                frameNanos += (spent - frameNanos) * 0.05;
            }
        }
        lastFrameNanos = now;
        frame++;
        shaders = IrisState.shaderPackInUse();
        if ((frame & 1023) == 1) {
            refreshRate = Math.max(Minecraft.getInstance().getWindow().getRefreshRate(), 120);
        }
        int hz = poseHz > 0 ? poseHz : refreshRate;
        int k = (int) (1.0E9 / hz / frameNanos);
        interval = !enabled || shaders || MEMORY == null ? 1 : Math.max(1, Math.min(k, maxInterval));
    }

    public static void render(EntityRenderDispatcher dispatcher, Entity entity, double x, double y, double z, float yaw, float partialTick,
                              PoseStack poseStack, MultiBufferSource buffers, int light, Renderer original) {
        int k = interval;
        if (k <= 1 || recording != null || !CoOConfig.vanillaReuseEntityMeshes || !eligible(dispatcher, entity)) {
            original.render(entity, x, y, z, yaw, partialTick, poseStack, buffers, light);
            return;
        }
        float smallPixels = EntityMeshReuse.smallPixels >= 0F ? EntityMeshReuse.smallPixels : CoOConfig.vanillaReuseSmallMobPixels;
        if (smallPixels > 0F) {
            double distance = Math.sqrt(x * x + y * y + z * z);
            float pixels = (float) (entity.getBbHeight() / Math.max(distance, 0.5) * 0.5 * RenderSystem.getProjectionMatrix().m11()
                    * Minecraft.getInstance().getWindow().getHeight());
            if (pixels < smallPixels) {
                k = Math.min(k * (pixels < smallPixels * 0.5F ? 3 : 2), maxSmallInterval);
            }
        }
        Cache cache = ((Holder) entity).coo$meshCache();
        if (replayOrRecord(cache, k, entity.getId() * 7, poseStack, x, y, z, buffers, light,
                recorder -> original.render(entity, x, y, z, yaw, partialTick, poseStack, recorder, light))
                && UNSAFE.add(entity.getType())) {
            LOGGER.info("Entity mesh reuse off for {}: its renderer draws outside the buffer source", EntityType.getKey(entity.getType()));
        }
    }

    public static void renderBlockEntity(BlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffers, Consumer<MultiBufferSource> original) {
        int k = interval;
        if (k <= 1 || recording != null || !CoOConfig.vanillaReuseBlockEntityMeshes || UNSAFE_BLOCK_ENTITIES.contains(blockEntity.getType())
                || blockEntity.isRemoved() || Minecraft.getInstance().level != blockEntity.getLevel()) {
            original.accept(buffers);
            return;
        }
        Cache cache = ((Holder) blockEntity).coo$meshCache();
        if (replayOrRecord(cache, k, Long.hashCode(blockEntity.getBlockPos().asLong()) * 7, poseStack, 0.0, 0.0, 0.0, buffers, 0, original)
                && UNSAFE_BLOCK_ENTITIES.add(blockEntity.getType())) {
            LOGGER.info("Block entity mesh reuse off for {}: its renderer draws outside the buffer source", BlockEntityType.getKey(blockEntity.getType()));
        }
    }

    private static boolean replayOrRecord(Cache cache, int k, int seed, PoseStack poseStack, double x, double y, double z,
                                          MultiBufferSource buffers, int light, Consumer<MultiBufferSource> original) {
        int phase = Math.floorMod(frame + seed, k);
        if (cache.valid && phase != 0 && frame - cache.frame < k && cache.buffers == buffers && cache.light == light
                && cache.replay(poseStack, x, y, z, buffers)) {
            replays++;
            return false;
        }
        cache.begin(poseStack, x, y, z, buffers, light);
        recording = cache;
        RECORDER.start(cache, buffers);
        try {
            original.accept(RECORDER);
        } catch (Throwable throwable) {
            cache.unsafe = true;
            throw throwable;
        } finally {
            RECORDER.stop();
            recording = null;
            cache.finish(frame);
        }
        records++;
        return cache.unsafe;
    }

    private static boolean eligible(EntityRenderDispatcher dispatcher, Entity entity) {
        if (entity instanceof Player || UNSAFE.contains(entity.getType())) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (entity == mc.getCameraEntity() || dispatcher.shouldRenderHitBoxes() || mc.shouldEntityAppearGlowing(entity)) {
            return false;
        }
        if (entity instanceof LivingEntity living && (living.hurtTime > 0 || living.deathTime > 0)) {
            return false;
        }
        return !(entity instanceof Mob mob) || !mob.isLeashed();
    }

    public static void directDraw() {
        if (recording != null && flushDepth == 0) {
            recording.unsafe = true;
        }
    }

    public static void flushBegin() {
        flushDepth++;
    }

    public static void flushEnd() {
        if (flushDepth > 0) {
            flushDepth--;
        }
    }

    @FunctionalInterface
    public interface Renderer {
        void render(Entity entity, double x, double y, double z, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light);
    }

    private static Unsafe unsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static ByteBuffer scratch(int bytes) {
        if (scratch.capacity() < bytes) {
            scratch = ByteBuffer.allocateDirect(Math.max(bytes, scratch.capacity() * 2));
        }
        return scratch;
    }

    public static final class Cache {
        private final Matrix4f inverse = new Matrix4f();
        private final Matrix3f inverseNormal = new Matrix3f();
        private double x;
        private double y;
        private double z;
        private MultiBufferSource buffers;
        private int light;
        private int frame;
        private boolean valid;
        boolean unsafe;

        private RenderType[] types = new RenderType[8];
        private VertexFormatDescription[] formats = new VertexFormatDescription[8];
        private int[] starts = new int[8];
        private int[] counts = new int[8];
        private int segments;
        private Object lastSink;
        private boolean lastRaw;

        private byte[] bytes = new byte[0];
        private int byteLength;
        private int[] generic = new int[0];
        private int genericLength;

        void begin(PoseStack poseStack, double x, double y, double z, MultiBufferSource buffers, int light) {
            PoseStack.Pose pose = poseStack.last();
            inverse.set(pose.pose()).invertAffine();
            inverseNormal.set(pose.normal()).invert();
            this.x = x;
            this.y = y;
            this.z = z;
            this.buffers = buffers;
            this.light = light;
            this.valid = false;
            this.unsafe = false;
            this.segments = 0;
            this.lastSink = null;
            this.byteLength = 0;
            this.genericLength = 0;
        }

        void finish(int frame) {
            this.frame = frame;
            this.valid = !unsafe && Float.isFinite(inverse.m00()) && Float.isFinite(inverseNormal.m00());
        }

        private int segment(Object sink, RenderType type, boolean raw, VertexFormatDescription format) {
            int last = segments - 1;
            if (last >= 0 && lastSink == sink && lastRaw == raw && (!raw || formats[last] == format)) {
                return last;
            }
            if (segments == types.length) {
                int size = segments * 2;
                types = java.util.Arrays.copyOf(types, size);
                formats = java.util.Arrays.copyOf(formats, size);
                starts = java.util.Arrays.copyOf(starts, size);
                counts = java.util.Arrays.copyOf(counts, size);
            }
            types[segments] = type;
            formats[segments] = raw ? format : null;
            starts[segments] = raw ? byteLength : genericLength;
            counts[segments] = 0;
            lastSink = sink;
            lastRaw = raw;
            return segments++;
        }

        void addRaw(Object sink, RenderType type, long pointer, int count, VertexFormatDescription format) {
            int length = count * format.stride();
            if (byteLength + length > bytes.length) {
                bytes = java.util.Arrays.copyOf(bytes, Math.max(byteLength + length, bytes.length * 2));
            }
            int index = segment(sink, type, true, format);
            MEMORY.copyMemory(null, pointer, bytes, BYTES + byteLength, length);
            byteLength += length;
            counts[index] += count;
        }

        void addGeneric(Object sink, RenderType type, int[] vertex) {
            if (genericLength + SLOTS > generic.length) {
                generic = java.util.Arrays.copyOf(generic, Math.max(generic.length * 2, SLOTS * 64));
            }
            int index = segment(sink, type, false, null);
            System.arraycopy(vertex, 0, generic, genericLength, SLOTS);
            genericLength += SLOTS;
            counts[index]++;
        }

        boolean replay(PoseStack poseStack, double x, double y, double z, MultiBufferSource buffers) {
            PoseStack.Pose pose = poseStack.last();
            Matrix4f delta = DELTA.set(pose.pose()).translate((float) (x - this.x), (float) (y - this.y), (float) (z - this.z)).mul(inverse);
            Matrix3f deltaNormal = DELTA_NORMAL.set(pose.normal()).mul(inverseNormal);
            for (int s = 0; s < segments; s++) {
                VertexConsumer consumer = buffers.getBuffer(types[s]);
                if (formats[s] != null) {
                    replayRaw(consumer, s, delta, deltaNormal);
                } else {
                    replayGeneric(consumer, s, delta, deltaNormal);
                }
            }
            return true;
        }

        private void replayRaw(VertexConsumer consumer, int s, Matrix4f delta, Matrix3f deltaNormal) {
            VertexFormatDescription format = formats[s];
            int stride = format.stride();
            int position = format.getElementOffset(CommonVertexAttribute.POSITION);
            int normal = format.containsElement(CommonVertexAttribute.NORMAL) ? format.getElementOffset(CommonVertexAttribute.NORMAL) : -1;
            VertexBufferWriter writer = VertexBufferWriter.tryOf(consumer);
            long source = BYTES + starts[s];
            int remaining = counts[s];
            ByteBuffer buffer = scratch(Math.min(remaining, CHUNK) * stride);
            long target = MemoryUtil.memAddress(buffer);
            float a00 = delta.m00(), a01 = delta.m01(), a02 = delta.m02();
            float a10 = delta.m10(), a11 = delta.m11(), a12 = delta.m12();
            float a20 = delta.m20(), a21 = delta.m21(), a22 = delta.m22();
            float a30 = delta.m30(), a31 = delta.m31(), a32 = delta.m32();
            float n00 = deltaNormal.m00, n01 = deltaNormal.m01, n02 = deltaNormal.m02;
            float n10 = deltaNormal.m10, n11 = deltaNormal.m11, n12 = deltaNormal.m12;
            float n20 = deltaNormal.m20, n21 = deltaNormal.m21, n22 = deltaNormal.m22;
            boolean turn = normal >= 0 && (Math.abs(n00 - 1.0F) > 1.0E-6F || Math.abs(n11 - 1.0F) > 1.0E-6F || Math.abs(n22 - 1.0F) > 1.0E-6F
                    || Math.abs(n01) > 1.0E-6F || Math.abs(n02) > 1.0E-6F || Math.abs(n10) > 1.0E-6F
                    || Math.abs(n12) > 1.0E-6F || Math.abs(n20) > 1.0E-6F || Math.abs(n21) > 1.0E-6F);
            while (remaining > 0) {
                int count = Math.min(remaining, CHUNK);
                MEMORY.copyMemory(bytes, source, null, target, (long) count * stride);
                for (int i = 0; i < count; i++) {
                    long vertex = target + (long) i * stride;
                    long p = vertex + position;
                    float px = MemoryUtil.memGetFloat(p);
                    float py = MemoryUtil.memGetFloat(p + 4);
                    float pz = MemoryUtil.memGetFloat(p + 8);
                    MemoryUtil.memPutFloat(p, a00 * px + a10 * py + a20 * pz + a30);
                    MemoryUtil.memPutFloat(p + 4, a01 * px + a11 * py + a21 * pz + a31);
                    MemoryUtil.memPutFloat(p + 8, a02 * px + a12 * py + a22 * pz + a32);
                    if (turn) {
                        int packed = MemoryUtil.memGetInt(vertex + normal);
                        float nx = NormI8.unpackX(packed);
                        float ny = NormI8.unpackY(packed);
                        float nz = NormI8.unpackZ(packed);
                        int turned = NormI8.pack(n00 * nx + n10 * ny + n20 * nz, n01 * nx + n11 * ny + n21 * nz, n02 * nx + n12 * ny + n22 * nz);
                        MemoryUtil.memPutInt(vertex + normal, (turned & 0x00FFFFFF) | (packed & 0xFF000000));
                    }
                }
                if (writer != null) {
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        writer.push(stack, target, count, format);
                    }
                } else {
                    decode(consumer, format, target, count);
                }
                source += (long) count * stride;
                remaining -= count;
            }
        }

        private static void decode(VertexConsumer consumer, VertexFormatDescription format, long target, int count) {
            int stride = format.stride();
            int position = format.getElementOffset(CommonVertexAttribute.POSITION);
            int color = format.containsElement(CommonVertexAttribute.COLOR) ? format.getElementOffset(CommonVertexAttribute.COLOR) : -1;
            int texture = format.containsElement(CommonVertexAttribute.TEXTURE) ? format.getElementOffset(CommonVertexAttribute.TEXTURE) : -1;
            int overlay = format.containsElement(CommonVertexAttribute.OVERLAY) ? format.getElementOffset(CommonVertexAttribute.OVERLAY) : -1;
            int light = format.containsElement(CommonVertexAttribute.LIGHT) ? format.getElementOffset(CommonVertexAttribute.LIGHT) : -1;
            int normal = format.containsElement(CommonVertexAttribute.NORMAL) ? format.getElementOffset(CommonVertexAttribute.NORMAL) : -1;
            for (int i = 0; i < count; i++) {
                long vertex = target + (long) i * stride;
                consumer.vertex(MemoryUtil.memGetFloat(vertex + position), MemoryUtil.memGetFloat(vertex + position + 4), MemoryUtil.memGetFloat(vertex + position + 8));
                if (color >= 0) {
                    int abgr = MemoryUtil.memGetInt(vertex + color);
                    consumer.color(abgr & 0xFF, (abgr >>> 8) & 0xFF, (abgr >>> 16) & 0xFF, abgr >>> 24);
                }
                if (texture >= 0) {
                    consumer.uv(MemoryUtil.memGetFloat(vertex + texture), MemoryUtil.memGetFloat(vertex + texture + 4));
                }
                if (overlay >= 0) {
                    int packed = MemoryUtil.memGetInt(vertex + overlay);
                    consumer.overlayCoords(packed & 0xFFFF, packed >>> 16);
                }
                if (light >= 0) {
                    int packed = MemoryUtil.memGetInt(vertex + light);
                    consumer.uv2(packed & 0xFFFF, packed >>> 16);
                }
                if (normal >= 0) {
                    int packed = MemoryUtil.memGetInt(vertex + normal);
                    consumer.normal(NormI8.unpackX(packed), NormI8.unpackY(packed), NormI8.unpackZ(packed));
                }
                consumer.endVertex();
            }
        }

        private void replayGeneric(VertexConsumer consumer, int s, Matrix4f delta, Matrix3f deltaNormal) {
            int[] data = generic;
            int end = starts[s] + counts[s] * SLOTS;
            for (int i = starts[s]; i < end; i += SLOTS) {
                int mask = data[i + 11];
                float px = Float.intBitsToFloat(data[i]);
                float py = Float.intBitsToFloat(data[i + 1]);
                float pz = Float.intBitsToFloat(data[i + 2]);
                float x = delta.m00() * px + delta.m10() * py + delta.m20() * pz + delta.m30();
                float y = delta.m01() * px + delta.m11() * py + delta.m21() * pz + delta.m31();
                float z = delta.m02() * px + delta.m12() * py + delta.m22() * pz + delta.m32();
                float nx = Float.intBitsToFloat(data[i + 8]);
                float ny = Float.intBitsToFloat(data[i + 9]);
                float nz = Float.intBitsToFloat(data[i + 10]);
                float tx = deltaNormal.m00 * nx + deltaNormal.m10 * ny + deltaNormal.m20 * nz;
                float ty = deltaNormal.m01 * nx + deltaNormal.m11 * ny + deltaNormal.m21 * nz;
                float tz = deltaNormal.m02 * nx + deltaNormal.m12 * ny + deltaNormal.m22 * nz;
                int color = data[i + 3];
                if ((mask & M_BULK) != 0) {
                    consumer.vertex(x, y, z, ((color & 0xFF) + 0.5F) / 255.0F, (((color >>> 8) & 0xFF) + 0.5F) / 255.0F,
                            (((color >>> 16) & 0xFF) + 0.5F) / 255.0F, ((color >>> 24) + 0.5F) / 255.0F, Float.intBitsToFloat(data[i + 4]), Float.intBitsToFloat(data[i + 5]), data[i + 6], data[i + 7], tx, ty, tz);
                    continue;
                }
                if ((mask & M_POS) != 0) {
                    consumer.vertex(x, y, z);
                }
                if ((mask & M_COLOR) != 0) {
                    consumer.color(color & 0xFF, (color >>> 8) & 0xFF, (color >>> 16) & 0xFF, color >>> 24);
                }
                if ((mask & M_UV) != 0) {
                    consumer.uv(Float.intBitsToFloat(data[i + 4]), Float.intBitsToFloat(data[i + 5]));
                }
                if ((mask & M_OVERLAY) != 0) {
                    consumer.overlayCoords(data[i + 6] & 0xFFFF, data[i + 6] >>> 16);
                }
                if ((mask & M_LIGHT) != 0) {
                    consumer.uv2(data[i + 7] & 0xFFFF, data[i + 7] >>> 16);
                }
                if ((mask & M_NORMAL) != 0) {
                    consumer.normal(tx, ty, tz);
                }
                consumer.endVertex();
            }
        }
    }

    private static final class Recorder extends MultiBufferSource.BufferSource {
        private MultiBufferSource delegate;
        private Cache cache;
        private Sink[] sinks = new Sink[8];
        private int used;

        Recorder() {
            super(new com.mojang.blaze3d.vertex.BufferBuilder(256), new java.util.HashMap<>());
        }

        void start(Cache cache, MultiBufferSource delegate) {
            this.cache = cache;
            this.delegate = delegate;
            this.used = 0;
        }

        void stop() {
            this.cache = null;
            for (int i = 0; i < used; i++) {
                sinks[i].cache = null;
            }
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            VertexConsumer target = delegate.getBuffer(type);
            Cache cache = this.cache;
            if (cache == null) {
                return target;
            }
            if (used == sinks.length) {
                sinks = java.util.Arrays.copyOf(sinks, used * 2);
            }
            Sink sink = sinks[used];
            if (sink == null) {
                sink = sinks[used] = new Sink();
            }
            used++;
            sink.cache = cache;
            sink.type = type;
            sink.delegate = target;
            sink.mask = 0;
            return sink;
        }

        @Override
        public void endLastBatch() {
            if (delegate instanceof MultiBufferSource.BufferSource source) {
                source.endLastBatch();
            }
        }

        @Override
        public void endBatch() {
            if (delegate instanceof MultiBufferSource.BufferSource source) {
                source.endBatch();
            }
        }

        @Override
        public void endBatch(RenderType type) {
            if (delegate instanceof MultiBufferSource.BufferSource source) {
                source.endBatch(type);
            }
        }
    }

    private static final class Sink implements VertexConsumer, VertexBufferWriter {
        private Cache cache;
        private RenderType type;
        private VertexConsumer delegate;
        private final int[] vertex = new int[SLOTS];
        private int mask;

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            delegate.vertex(x, y, z);
            vertex[0] = Float.floatToRawIntBits((float) x);
            vertex[1] = Float.floatToRawIntBits((float) y);
            vertex[2] = Float.floatToRawIntBits((float) z);
            mask |= M_POS;
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            delegate.color(red, green, blue, alpha);
            vertex[3] = (red & 0xFF) | (green & 0xFF) << 8 | (blue & 0xFF) << 16 | (alpha & 0xFF) << 24;
            mask |= M_COLOR;
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            delegate.uv(u, v);
            vertex[4] = Float.floatToRawIntBits(u);
            vertex[5] = Float.floatToRawIntBits(v);
            mask |= M_UV;
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            delegate.overlayCoords(u, v);
            vertex[6] = (u & 0xFFFF) | v << 16;
            mask |= M_OVERLAY;
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            delegate.uv2(u, v);
            vertex[7] = (u & 0xFFFF) | v << 16;
            mask |= M_LIGHT;
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            delegate.normal(x, y, z);
            vertex[8] = Float.floatToRawIntBits(x);
            vertex[9] = Float.floatToRawIntBits(y);
            vertex[10] = Float.floatToRawIntBits(z);
            mask |= M_NORMAL;
            return this;
        }

        @Override
        public void endVertex() {
            delegate.endVertex();
            Cache cache = this.cache;
            if (cache != null) {
                vertex[11] = mask;
                cache.addGeneric(this, type, vertex);
            }
            mask = 0;
        }

        @Override
        public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v,
                           int overlay, int light, float normalX, float normalY, float normalZ) {
            delegate.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
            Cache cache = this.cache;
            if (cache == null) {
                return;
            }
            int[] data = vertex;
            data[0] = Float.floatToRawIntBits(x);
            data[1] = Float.floatToRawIntBits(y);
            data[2] = Float.floatToRawIntBits(z);
            data[3] = ((int) (red * 255.0F) & 0xFF) | ((int) (green * 255.0F) & 0xFF) << 8 | ((int) (blue * 255.0F) & 0xFF) << 16
                    | ((int) (alpha * 255.0F) & 0xFF) << 24;
            data[4] = Float.floatToRawIntBits(u);
            data[5] = Float.floatToRawIntBits(v);
            data[6] = overlay;
            data[7] = light;
            data[8] = Float.floatToRawIntBits(normalX);
            data[9] = Float.floatToRawIntBits(normalY);
            data[10] = Float.floatToRawIntBits(normalZ);
            data[11] = M_BULK;
            cache.addGeneric(this, type, data);
            mask = 0;
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
            delegate.defaultColor(red, green, blue, alpha);
            if (cache != null) {
                cache.unsafe = true;
            }
        }

        @Override
        public void unsetDefaultColor() {
            delegate.unsetDefaultColor();
        }

        @Override
        public VertexConsumer misc(VertexFormatElement element, int... data) {
            delegate.misc(element, data);
            if (cache != null) {
                cache.unsafe = true;
            }
            return this;
        }

        @Override
        public void push(MemoryStack stack, long pointer, int count, VertexFormatDescription format) {
            Cache cache = this.cache;
            if (cache != null) {
                cache.addRaw(this, type, pointer, count, format);
            }
            ((VertexBufferWriter) delegate).push(stack, pointer, count, format);
        }

        @Override
        public boolean isFullWriter() {
            return delegate instanceof VertexBufferWriter writer && writer.isFullWriter();
        }

        @Override
        public boolean canUseIntrinsics() {
            return delegate instanceof VertexBufferWriter writer && writer.canUseIntrinsics();
        }
    }
}
