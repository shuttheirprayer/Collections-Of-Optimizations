package com.misanthropy.collections_of_optimizations.core;

import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.LoadingModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoOMixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final Set<String> WARNED_GROUPS = new HashSet<>();

    private static final String MIXIN_PACKAGE = "com.misanthropy.collections_of_optimizations.mixin.";

    private static final Map<String, String> GROUP_TO_MODID = Map.ofEntries(
            Map.entry("curios", "curios"),
            Map.entry("oddaccessories", "oddaccessories"),
            Map.entry("artifacts", "artifacts"),
            Map.entry("caelus", "caelus"),
            Map.entry("lootr", "lootr"),
            Map.entry("blockswap", "blockswap"),
            Map.entry("justdirethings", "justdirethings"),
            Map.entry("dungeoncrawl", "dungeoncrawl"),
            Map.entry("goety", "goety"),
            Map.entry("shouldersurfing", "shouldersurfing"),
            Map.entry("mythsandlegends", "mythsandlegends"),
            Map.entry("ambientsounds", "ambientsounds"),
            Map.entry("arsnouveau", "ars_nouveau"),
            Map.entry("arselemental", "ars_elemental"),
            Map.entry("goetydelight", "goetydelight"),
            Map.entry("regionsunexplored", "regions_unexplored"),
            Map.entry("endinglibrary", "ending_library"),
            Map.entry("goetyrevelation", "goety_revelation"),
            Map.entry("revelationfix", "revelationfix"),
            Map.entry("geckolib", "geckolib"),
            Map.entry("gnetum", "gnetum"),
            Map.entry("tooltipoverhaul", "tooltipoverhaul"),
            Map.entry("cablefacades", "cable_facades"),
            Map.entry("oculus", "oculus"),
            Map.entry("naturesaura", "naturesaura"),
            Map.entry("xaerominimap", "xaerominimap"),
            Map.entry("w2w2", "w2w2"),
            Map.entry("terrablender", "terrablender"),
            Map.entry("terramity", "terramity"),
            Map.entry("armageddon", "armageddon_mod"),
            Map.entry("borninchaos", "born_in_chaos_v1"),
            Map.entry("brutalbosses", "brutalbosses"),
            Map.entry("brutality", "brutality"),
            Map.entry("bossesrise", "block_factorys_bosses"),
            Map.entry("soulsweapons", "soulsweapons"),
            Map.entry("konweapon", "weapon"),
            Map.entry("immersiveaircraft", "immersive_aircraft"),
            Map.entry("ftbchunks", "ftbchunks"),
            Map.entry("ftbxmodcompat", "ftbxmodcompat"),
            Map.entry("structurify", "structurify"),
            Map.entry("bloodmagic", "bloodmagic"),
            Map.entry("animus", "animus"),
            Map.entry("patchouli", "patchouli"),
            Map.entry("placebo", "placebo"),
            Map.entry("punchy", "punchy"),
            Map.entry("l2hostility", "l2hostility"),
            Map.entry("l2complements", "l2complements"),
            Map.entry("integratedapi", "integrated_api"),
            Map.entry("elysiumapi", "elysium_api"),
            Map.entry("cerbonsapi", "cerbons_api"),
            Map.entry("echelon", "tiered"),
            Map.entry("enigmaticdice", "enigmaticdice"),
            Map.entry("balm", "balm"),
            Map.entry("macabre", "macabre"),
            Map.entry("alexsmobs", "alexsmobs"),
            Map.entry("alexscaves", "alexscaves"),
            Map.entry("adastra", "ad_astra"),
            Map.entry("zeta", "zeta"),
            Map.entry("quark", "quark"),
            Map.entry("perception", "perception"),
            Map.entry("pehkui", "pehkui"),
            Map.entry("relics", "relics"),
            Map.entry("morerelics", "morerelics"),
            Map.entry("terracurio", "confluence"),
            Map.entry("cosmeticarmor", "cosmeticarmorreworked"),
            Map.entry("morehitboxes", "morehitboxes"),
            Map.entry("moonlight", "moonlight"),
            Map.entry("subtleeffects", "subtle_effects"),
            Map.entry("tonsofenchants", "tonsofenchants"),
            Map.entry("arseng", "arseng"),
            Map.entry("saintsdragons", "saintsdragons"),
            Map.entry("emf", "entity_model_features"),
            Map.entry("etf", "entity_texture_features"),
            Map.entry("iceandfire", "iceandfire"),
            Map.entry("iafdragonfix", "iceandfire"),
            Map.entry("mowziesmobs", "mowziesmobs"),
            Map.entry("pickupnotifier", "pickupnotifier"),
            Map.entry("photon", "photon"),
            Map.entry("immediatelyfast", "immediatelyfast"),
            Map.entry("modernfix", "modernfix"),
            Map.entry("fancymenu", "fancymenu"),
            Map.entry("ironslib", "irons_lib"),
            Map.entry("bettercombat", "bettercombat"),
            Map.entry("cofh", "cofh_core"),
            Map.entry("xaerolib", "xaerolib"),
            Map.entry("create", "create"),
            Map.entry("cataclysm", "cataclysm"),
            Map.entry("dodosmobs", "dodosmobs"),
            Map.entry("eeeabsmobs", "eeeabsmobs"),
            Map.entry("fromtheshadows", "fromtheshadows"),
            Map.entry("gtbcs", "gtbcs_spell_lib"),
            Map.entry("legendarymonsters", "legendary_monsters"),
            Map.entry("xaeroworldmap", "xaeroworldmap"),
            Map.entry("supplementaries", "supplementaries"),
            Map.entry("amendments", "amendments"),
            Map.entry("copycats", "copycats"),
            Map.entry("biomeswevegone", "biomeswevegone"),
            Map.entry("distanthorizons", "distanthorizons"),
            Map.entry("industrialforegoing", "industrialforegoing"),
            Map.entry("enigmaticaddons", "enigmaticaddons"),
            Map.entry("enigmaticdelicacy", "enigmaticdelicacy"),
            Map.entry("enigmaticlegacy", "enigmaticlegacy"),
            Map.entry("traveloptics", "traveloptics"),
            Map.entry("celestialcore", "celestial_core"),
            Map.entry("celestialenchantments", "celestial_enchantments"),
            Map.entry("uniqueaccessories", "uniqueaccessories"),
            Map.entry("biolith", "biolith"),
            Map.entry("skarriermobs", "skarrier_mobs"),
            Map.entry("createsolar", "createsolar"),
            Map.entry("summonity", "summonity"),
            Map.entry("cnc", "cnc"),
            Map.entry("crittersandcompanions", "crittersandcompanions"),
            Map.entry("cucumber", "cucumber"),
            Map.entry("mysticalagriculture", "mysticalagriculture"),
            Map.entry("dynamictrees", "dynamictrees"),
            Map.entry("konkrete", "konkrete"),
            Map.entry("titanium", "titanium"),
            Map.entry("lithostitched", "lithostitched"),
            Map.entry("domesticationinnovation", "domesticationinnovation"),
            Map.entry("moreartifacts", "moreartifacts"),
            Map.entry("simplymore", "simplymore"),
            Map.entry("simplyswords", "simplyswords"),
            Map.entry("celestisynth", "celestisynth"),
            Map.entry("covenantoftheseven", "covenant_of_the_seven"),
            Map.entry("hexerei", "hexerei"),
            Map.entry("easyvillagers", "easy_villagers"),
            Map.entry("companions", "companions"),
            Map.entry("manybows", "too_many_bows"),
            Map.entry("immersivearmors", "immersive_armors"),
            Map.entry("starcatcher", "starcatcher"),
            Map.entry("neobackports", "neobackports"),
            Map.entry("faunify", "faunify"),
            Map.entry("textanimator", "textanimator"),
            Map.entry("darkdoppelganger", "darkdoppelganger"),
            Map.entry("torchmaster", "torchmaster"),
            Map.entry("cognition", "experienceobelisk"),
            Map.entry("opposingforce", "opposing_force"),
            Map.entry("peyroscythe", "peyroscythe"),
            Map.entry("somakespells", "somakespells"),
            Map.entry("biomancy", "biomancy"),
            Map.entry("notenoughanimations", "notenoughanimations"),
            Map.entry("lethality", "lethality"),
            Map.entry("castercuriosbonus", "caster_curios_bonus"),
            Map.entry("legendaryspellbooks", "legendary_spellbooks"),
            Map.entry("meetyourfight", "meetyourfight"),
            Map.entry("bmd", "bosses_of_mass_destruction"),
            Map.entry("mahoutsukai", "mahoutsukai"),
            Map.entry("primitivemobs", "primitive_mobs"),
            Map.entry("ribbits", "ribbits"),
            Map.entry("hazennstuff", "hazennstuff"),
            Map.entry("species", "species"),
            Map.entry("cookingforblockheads", "cookingforblockheads"),
            Map.entry("integratedvillages", "integrated_villages"),
            Map.entry("deeperdarker", "deeperdarker"),
            Map.entry("goetyhostility", "goetyhostility"),
            Map.entry("fdbosses", "fdbosses"),
            Map.entry("mutantmonsters", "mutantmonsters"),
            Map.entry("cnb", "cnb"),
            Map.entry("mobgrindingutils", "mob_grinding_utils"),
            Map.entry("transmog", "transmog"),
            Map.entry("ironsspellbooks", "irons_spellbooks"),
            Map.entry("hostilenetworks", "hostilenetworks"),
            Map.entry("extrahnn", "extrahnn"),
            Map.entry("l2archery", "l2archery"),
            Map.entry("blessfulled", "blessfulled"),
            Map.entry("tomeofblood", "tomeofblood"),
            Map.entry("betterspellcasting", "better_spellcasting"),
            Map.entry("arsadditions", "ars_additions"),
            Map.entry("adamsarsplus", "adamsarsplus"),
            Map.entry("duplicationless", "duplicationless"),
            Map.entry("fluidium", "fluidium"),
            Map.entry("corpse", "corpse"),
            Map.entry("storagedrawers", "storagedrawers"),
            Map.entry("skyarena", "skyarena")
    );

    private static final Map<String, Integer> GROUP_MIN_MAJOR = Map.of(
            "structurify", 2,
            "iceandfire", 2,
            "iafdragonfix", 2
    );

    private static final Map<String, String[]> GROUP_CONFLICTS = Map.of(
            "geckolib", new String[]{"gbf", "geckolib_animation_optimizer"},

            "iafdragonfix", new String[]{"iafdragonfix"}
    );

    private static final Map<String, String[]> MIXIN_CONFLICTS = Map.of(

            "vanilla.MixinStructureCheck", new String[]{"modernfix"},
            "vanilla.MixinServerLevelStructureState", new String[]{"modernfix"},

            "vanilla.MixinItemEntityRenderer", new String[]{"flerovium"},

            "vanilla.MixinChunkMapEntityTracker", new String[]{"vmp", "hariplayer"},

            "vanilla.MixinClientLevelBiomeBlend", new String[]{"betterbiomeblend", "betterbiomereblend"},

            // Lomka @Overwrites these three methods; injecting into a merged method at equal priority is a hard error.
            "vanilla.MixinPoseStackAxisRotation", new String[]{"lomka"},
            "vanilla.MixinBlockPosBetweenClosed", new String[]{"lomka"},
            "vanilla.MixinPalettedContainerUniform", new String[]{"lomka"}
    );

    private static final Map<String, String[]> MIXIN_REQUIRES = Map.ofEntries(
            Map.entry("elysiumapi.MixinElysiumTerrablenderHelper", new String[]{"terrablender"}),

            Map.entry("goetyrevelation.MixinATAHelper", new String[]{"curios"}),

            Map.entry("goety.MixinCuriosFinder", new String[]{"curios"}),

            Map.entry("goety.MixinCuriosFinderFilter", new String[]{"curios"}),

            Map.entry("morerelics.MixinMoreRelicsUtil", new String[]{"curios", "relics"}),

            Map.entry("modernfix.MixinJEIRuntimeCapturer", new String[]{"jei"}),

            Map.entry("w2w2.MixinXaeronCompatibility", new String[]{"xaerominimap"}),

            Map.entry("summonity.MixinSoloistsSealItem", new String[]{"curios"}),

            Map.entry("summonity.MixinPolychromicNecklaceItem", new String[]{"curios"}),

            Map.entry("summonity.MixinConductiveBatteryItem", new String[]{"curios"}),

            Map.entry("enigmaticlegacy.MixinSuperpositionCurioLookup", new String[]{"curios"}),

            Map.entry("lethality.MixinLethalityCharmScan", new String[]{"curios"}),

            Map.entry("castercuriosbonus.MixinCcbCurioLookup", new String[]{"curios"}),

            Map.entry("legendaryspellbooks.MixinLsTornadoTracker", new String[]{"legendary_monsters"}),

            Map.entry("legendaryspellbooks.MixinLsTornadoScan", new String[]{"legendary_monsters"}),

            Map.entry("meetyourfight.MixinMyfCurioLookup", new String[]{"curios"}),

            Map.entry("meetyourfight.MixinMyfSpectresEye", new String[]{"curios"}),

            Map.entry("copycats.MixinBlockRendererColorContext", new String[]{"embeddium"}),

            Map.entry("ironsspellbooks.MixinIsbCurioEquipCheck", new String[]{"curios"})
    );

    private static final Map<String, String[]> MIXIN_ANY_OF = Map.of(
            "elysiumapi.MixinClimateSampler", new String[]{"elysium_api", "alexscaves"}
    );

    private static final Map<String, Boolean> LOADED_CACHE = new HashMap<>();
    private static final Map<String, Integer> MAJOR_CACHE = new HashMap<>();

    @Override
    public void onLoad(String mixinPackage) {
        if (!isModPresent("mcreator_mem_fix")) {
            McreatorVariableTransformer.install();
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(MIXIN_PACKAGE)) {
            return true;
        }
        String remainder = mixinClassName.substring(MIXIN_PACKAGE.length());
        int separator = remainder.indexOf('.');
        if (separator < 0) {
            return true;
        }
        String group = remainder.substring(0, separator);
        String[] anyOf = MIXIN_ANY_OF.get(remainder);
        if (anyOf != null) {
            boolean found = false;
            for (String candidate : anyOf) {
                if (isModPresent(candidate)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        } else {
            String modId = GROUP_TO_MODID.get(group);
            if (modId != null && !isModPresent(modId)) {
                return false;
            }
            Integer minMajor = GROUP_MIN_MAJOR.get(group);
            if (modId != null && minMajor != null && majorVersion(modId) < minMajor) {
                if (WARNED_GROUPS.add(group)) {
                    LOGGER.warn("Disabling '{}' optimizations: installed '{}' version is unsupported{}",
                            group, modId,
                            "iceandfire".equals(modId)
                                    ? " (Ice and Fire: Community Edition is not supported - use the original Ice and Fire mod to enable these optimizations)"
                                    : "");
                }
                return false;
            }
        }
        String[] conflicts = GROUP_CONFLICTS.get(group);
        if (conflicts != null) {
            for (String conflict : conflicts) {
                if (isModPresent(conflict)) {
                    return false;
                }
            }
        }
        String[] mixinConflicts = MIXIN_CONFLICTS.get(remainder);
        if (mixinConflicts != null) {
            for (String conflict : mixinConflicts) {
                if (isModPresent(conflict)) {
                    return false;
                }
            }
        }
        String[] required = MIXIN_REQUIRES.get(remainder);
        if (required != null) {
            for (String dependency : required) {
                if (!isModPresent(dependency)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static synchronized boolean isModPresent(String modId) {
        Boolean cached = LOADED_CACHE.get(modId);
        if (cached != null) {
            return cached;
        }
        boolean present;
        try {
            present = LoadingModList.get().getModFileById(modId) != null;
        } catch (Throwable throwable) {
            present = false;
        }
        LOADED_CACHE.put(modId, present);
        return present;
    }

    private static synchronized int majorVersion(String modId) {
        Integer cached = MAJOR_CACHE.get(modId);
        if (cached != null) {
            return cached;
        }
        int major = Integer.MAX_VALUE;
        try {
            ModFileInfo modFile = LoadingModList.get().getModFileById(modId);
            if (modFile != null) {
                for (IModInfo mod : modFile.getMods()) {
                    if (!mod.getModId().equals(modId)) {
                        continue;
                    }
                    String version = mod.getVersion().toString();
                    int end = 0;
                    while (end < version.length() && Character.isDigit(version.charAt(end))) {
                        end++;
                    }
                    if (end > 0) {
                        major = Integer.parseInt(version.substring(0, Math.min(end, 9)));
                    }
                    break;
                }
            }
        } catch (Throwable throwable) {
            major = Integer.MAX_VALUE;
        }
        MAJOR_CACHE.put(modId, major);
        return major;
    }
}
