package com.misanthropy.collections_of_optimizations.core;

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

    private final ClassValue<Boolean> cache;

    private ModEntityFilter(String packagePrefix) {
        this.cache = new ClassValue<>() {
            @Override
            protected Boolean computeValue(Class<?> type) {
                for (Class<?> current = type; current != null; current = current.getSuperclass()) {
                    if (current.getName().startsWith(packagePrefix)) {
                        return Boolean.TRUE;
                    }
                }
                return Boolean.FALSE;
            }
        };
    }

    public boolean matches(Object entity) {
        return entity != null && this.cache.get(entity.getClass());
    }
}
