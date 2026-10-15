package com.misanthropy.collections_of_optimizations.core;

import java.util.concurrent.ConcurrentHashMap;

public final class ModEntityFilter {

    public static final ModEntityFilter TERRAMITY = new ModEntityFilter("net.mcreator.terramity.entity.");
    public static final ModEntityFilter ARMAGEDDON = new ModEntityFilter("net.mcreator.armageddonmod.entity.");
    public static final ModEntityFilter BORN_IN_CHAOS = new ModEntityFilter("net.mcreator.borninchaosv.entity.");
    public static final ModEntityFilter BOSSES_RISE = new ModEntityFilter("net.unusual.blockfactorysbosses.entity.");
    public static final ModEntityFilter MACABRE = new ModEntityFilter("com.curseforge.macabre.entity.");
    public static final ModEntityFilter SKARRIER_MOBS = new ModEntityFilter("net.mcreator.skarriermobs.entity.");
    public static final ModEntityFilter CRITTERS_N_CRAWLERS = new ModEntityFilter("net.imasillylittleguy.cnc.entity.");
    public static final ModEntityFilter CNC_CARIBOU = new ModEntityFilter("net.imasillylittleguy.cnc.entity.CaribouEntity");
    public static final ModEntityFilter CNC_WECHUGE = new ModEntityFilter("net.imasillylittleguy.cnc.entity.WechugeEntity");
    public static final ModEntityFilter COMPANIONS = new ModEntityFilter("dev.xylonity.companions.");
    public static final ModEntityFilter GOETY_HOSTILITY_WILDFIRE = new ModEntityFilter("com.ratrod.goetyhostility.common.entities.hostile.HostileWildfire");

    private final ConcurrentHashMap<Class<?>, Boolean> cache = new ConcurrentHashMap<>();
    private final String packagePrefix;

    private ModEntityFilter(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    public boolean matches(Object entity) {
        if (entity == null) {
            return false;
        }
        Class<?> type = entity.getClass();
        Boolean hit = this.cache.get(type);
        if (hit == null) {
            hit = this.compute(type);
            this.cache.put(type, hit);
        }
        return hit;
    }

    private Boolean compute(Class<?> type) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            if (current.getName().startsWith(this.packagePrefix)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
