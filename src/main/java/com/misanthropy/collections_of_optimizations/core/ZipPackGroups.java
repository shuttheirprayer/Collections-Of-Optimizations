package com.misanthropy.collections_of_optimizations.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipPackGroups {

    private static final ZipEntry[] NONE = new ZipEntry[0];

    private final ZipFile zip;
    private final Map<String, ZipEntry[]> groups;

    private ZipPackGroups(ZipFile zip, Map<String, ZipEntry[]> groups) {
        this.zip = zip;
        this.groups = groups;
    }

    public boolean covers(ZipFile zip) {
        return this.zip == zip;
    }

    public ZipEntry[] entriesUnder(String prefix) {
        String key = key(prefix);
        if (key == null) {
            return NONE;
        }
        ZipEntry[] group = groups.get(key);
        return group == null ? NONE : group;
    }

    public static ZipPackGroups build(ZipFile zip) {
        Map<String, List<ZipEntry>> lists = new HashMap<>();
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            String key = key(entry.getName());
            if (key != null) {
                lists.computeIfAbsent(key, k -> new ArrayList<>()).add(entry);
            }
        }
        Map<String, ZipEntry[]> groups = new HashMap<>(lists.size() * 2);
        for (Map.Entry<String, List<ZipEntry>> e : lists.entrySet()) {
            groups.put(e.getKey(), e.getValue().toArray(NONE));
        }
        return new ZipPackGroups(zip, groups);
    }

    public static String key(String name) {
        int first = name.indexOf('/');
        int second = first < 0 ? -1 : name.indexOf('/', first + 1);
        int third = second < 0 ? -1 : name.indexOf('/', second + 1);
        return third < 0 ? null : name.substring(0, third + 1);
    }
}
