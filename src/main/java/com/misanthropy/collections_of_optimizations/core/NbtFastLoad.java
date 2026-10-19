package com.misanthropy.collections_of_optimizations.core;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class NbtFastLoad {

    public static boolean enabled = true;
    public static boolean removeByteBufTagLimit = true;
    public static boolean presizeCompounds = true;
    public static int maxCompoundDepth = Integer.MAX_VALUE;
    public static int maxListDepth = Integer.MAX_VALUE;
    public static Function<Map<String, Tag>, CompoundTag> compoundFactory;
    public static BiFunction<List<Tag>, Byte, ListTag> listFactory;

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final int MAX_CACHED = 64;
    private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);

    private NbtFastLoad() {
    }

    public static TagType<CompoundTag> compoundType(TagType<CompoundTag> fallback) {
        return new Type<>(fallback, true);
    }

    public static TagType<ListTag> listType(TagType<ListTag> fallback) {
        return new Type<>(fallback, false);
    }

    public static void logState() {
        LOGGER.info("NBT loading: compound {}, list {}", owner(TagTypes.getType(10)), owner(TagTypes.getType(9)));
    }

    private static String owner(TagType<?> type) {
        return type instanceof Type ? "Collections of Optimizations" : type.getClass().getName();
    }

    private static final class Type<T extends Tag> implements TagType.VariableSize<T> {
        private final TagType<T> fallback;
        private final boolean compound;

        Type(TagType<T> fallback, boolean compound) {
            this.fallback = fallback;
            this.compound = compound;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T load(DataInput input, int depth, NbtAccounter accounter) throws IOException {
            if (!enabled) {
                return fallback.load(input, depth, accounter);
            }
            return (T) (compound ? loadCompound(input, depth, accounter) : loadList(input, depth, accounter));
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput input, StreamTagVisitor visitor) throws IOException {
            return fallback.parse(input, visitor);
        }

        @Override
        public void skip(DataInput input) throws IOException {
            fallback.skip(input);
        }

        @Override
        public boolean isValue() {
            return fallback.isValue();
        }

        @Override
        public String getName() {
            return fallback.getName();
        }

        @Override
        public String getPrettyName() {
            return fallback.getPrettyName();
        }
    }

    private static CompoundTag loadCompound(DataInput input, int depth, NbtAccounter accounter) throws IOException {
        if (depth > maxCompoundDepth) {
            LOGGER.warn("Tried to read NBT tag with too high complexity, depth: {}", depth);
            return new CompoundTag();
        }
        accounter.accountBytes(48L);
        Scratch scratch = SCRATCH.get();
        if (!presizeCompounds) {
            return loadCompoundGrowing(input, depth, accounter, scratch);
        }
        int base = scratch.top;
        try {
            while (true) {
                accounter.accountBytes(2L);
                byte id = input.readByte();
                if (id == 0) {
                    break;
                }
                String key = accounter.readUTF(readKey(input, scratch));
                accounter.accountBytes(28 + 2 * key.length());
                accounter.accountBytes(4L);
                scratch.push(key, readValue(input, depth, accounter, id, key));
            }
            int top = scratch.top;
            Object[] pairs = scratch.pairs;
            Map<String, Tag> map = new Object2ObjectOpenHashMap<>((top - base) >> 1);
            int added = 0;
            for (int i = base; i < top; i += 2) {
                if (map.put((String) pairs[i], (Tag) pairs[i + 1]) == null) {
                    added++;
                }
            }
            accounter.accountBytes(36L * added);
            return compoundFactory.apply(map);
        } finally {
            Arrays.fill(scratch.pairs, base, scratch.top, null);
            scratch.top = base;
        }
    }

    private static CompoundTag loadCompoundGrowing(DataInput input, int depth, NbtAccounter accounter, Scratch scratch) throws IOException {
        Map<String, Tag> map = new Object2ObjectOpenHashMap<>(2);
        while (true) {
            accounter.accountBytes(2L);
            byte id = input.readByte();
            if (id == 0) {
                break;
            }
            String key = accounter.readUTF(readKey(input, scratch));
            accounter.accountBytes(28 + 2 * key.length());
            accounter.accountBytes(4L);
            Tag tag = readValue(input, depth, accounter, id, key);
            if (map.put(key, tag) == null) {
                accounter.accountBytes(36L);
            }
        }
        return compoundFactory.apply(map);
    }

    private static Tag readValue(DataInput input, int depth, NbtAccounter accounter, byte id, String key) throws IOException {
        TagType<?> type = TagTypes.getType(id);
        try {
            return type.load(input, depth + 1, accounter);
        } catch (IOException e) {
            CrashReport report = CrashReport.forThrowable(e, "Loading NBT data");
            CrashReportCategory category = report.addCategory("NBT Tag");
            category.setDetail("Tag name", key);
            category.setDetail("Tag type", type.getName());
            throw new ReportedException(report);
        }
    }

    private static ListTag loadList(DataInput input, int depth, NbtAccounter accounter) throws IOException {
        if (depth > maxListDepth) {
            LOGGER.warn("Tried to read NBT tag with too high complexity, depth: {}", depth);
            return new ListTag();
        }
        accounter.accountBytes(37L);
        byte id = input.readByte();
        int count = input.readInt();
        if (id == 0 && count > 0) {
            throw new RuntimeException("Missing type on ListTag");
        }
        accounter.accountBytes(4L * (long) count);
        TagType<?> type = TagTypes.getType(id);
        List<Tag> list = Lists.newArrayListWithCapacity(count);
        for (int i = 0; i < count; i++) {
            list.add(type.load(input, depth + 1, accounter));
        }
        return listFactory.apply(list, id);
    }

    private static String readKey(DataInput input, Scratch scratch) throws IOException {
        if (!(input instanceof DataInputStream)) {
            return input.readUTF();
        }
        int length = input.readUnsignedShort();
        byte[] bytes = scratch.bytes;
        if (length > bytes.length) {
            bytes = length <= 1024 ? (scratch.bytes = new byte[1024]) : new byte[length];
        }
        input.readFully(bytes, 0, length);
        int hash = 0;
        for (int i = 0; i < length; i++) {
            byte b = bytes[i];
            if (b < 0) {
                return decode(bytes, length);
            }
            hash = 31 * hash + b;
        }
        if (length > MAX_CACHED) {
            return new String(bytes, 0, length, StandardCharsets.ISO_8859_1);
        }
        String[] keys = scratch.keys;
        int slot = (hash ^ (hash >>> 16)) & (keys.length - 1);
        String cached = keys[slot];
        if (cached != null && cached.length() == length) {
            int i = 0;
            while (i < length && cached.charAt(i) == bytes[i]) {
                i++;
            }
            if (i == length) {
                return cached;
            }
        }
        String fresh = new String(bytes, 0, length, StandardCharsets.ISO_8859_1);
        keys[slot] = fresh;
        return fresh;
    }

    private static String decode(byte[] bytes, int length) throws IOException {
        byte[] framed = new byte[length + 2];
        framed[0] = (byte) (length >>> 8);
        framed[1] = (byte) length;
        System.arraycopy(bytes, 0, framed, 2, length);
        return DataInputStream.readUTF(new DataInputStream(new ByteArrayInputStream(framed)));
    }

    private static final class Scratch {
        final String[] keys = new String[1024];
        byte[] bytes = new byte[256];
        Object[] pairs = new Object[64];
        int top;

        void push(String key, Tag tag) {
            int at = this.top;
            if (at + 2 > this.pairs.length) {
                this.pairs = Arrays.copyOf(this.pairs, this.pairs.length << 1);
            }
            this.pairs[at] = key;
            this.pairs[at + 1] = tag;
            this.top = at + 2;
        }
    }
}
