package com.misanthropy.collections_of_optimizations.core;

import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.LoadingModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoOMixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final Set<String> WARNED_GROUPS = new HashSet<>();

    private static final String MIXIN_PACKAGE = "com.misanthropy.collections_of_optimizations.mixin.";
    private static final String ELYSIUM_SOUND_CACHE_MIXIN = MIXIN_PACKAGE + "elysiumapi.MixinBlockSoundTransformerCache";
    private static final String ELYSIUM_BLOCK_MIXIN = "net.jadenxgamer.elysium_api.impl.mixin.block.BlockMixin";
    private static final String ELYSIUM_HANDLER_DESC = "(Lnet/minecraft/world/level/block/state/BlockState;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V";
    private static final String ELYSIUM_DISPATCH_OWNER = "com/misanthropy/collections_of_optimizations/core/ElysiumSoundTransformers";
    private static final String MIXIN_MERGED_DESC = "Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;";

    private static final Map<String, String> GROUP_TO_MODID = Map.ofEntries(
            Map.entry("radium", "radium"),
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
            Map.entry("apothicattributes", "attributeslib"),
            Map.entry("arsnouveau", "ars_nouveau"),
            Map.entry("arselemental", "ars_elemental"),
            Map.entry("goetydelight", "goetydelight"),
            Map.entry("regionsunexplored", "regions_unexplored"),
            Map.entry("endinglibrary", "ending_library"),
            Map.entry("goetyrevelation", "goety_revelation"),
            Map.entry("revelationfix", "revelationfix"),
            Map.entry("geckolib", "geckolib"),
            Map.entry("ldlib", "ldlib"),
            Map.entry("gnetum", "gnetum"),
            Map.entry("tooltipoverhaul", "tooltipoverhaul"),
            Map.entry("cablefacades", "cable_facades"),
            Map.entry("oculus", "oculus"),
            Map.entry("citadel", "citadel"),
            Map.entry("asyncparticles", "asyncparticles"),
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
            Map.entry("jei", "jei"),
            Map.entry("ftbxmodcompat", "ftbxmodcompat"),
            Map.entry("structurify", "structurify"),
            Map.entry("bloodmagic", "bloodmagic"),
            Map.entry("animus", "animus"),
            Map.entry("patchouli", "patchouli"),
            Map.entry("placebo", "placebo"),
            Map.entry("punchy", "punchy"),
            Map.entry("embeddium", "embeddium"),
            Map.entry("alltheleaks", "alltheleaks"),
            Map.entry("l2hostility", "l2hostility"),
            Map.entry("l2complements", "l2complements"),
            Map.entry("integratedapi", "integrated_api"),
            Map.entry("elysiumapi", "elysium_api"),
            Map.entry("cerbonsapi", "cerbons_api"),
            Map.entry("echelon", "tiered"),
            Map.entry("balm", "balm"),
            Map.entry("macabre", "macabre"),
            Map.entry("alexsmobs", "alexsmobs"),
            Map.entry("alexscaves", "alexscaves"),
            Map.entry("architectury", "architectury"),
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
            Map.entry("immersiveweathering", "immersive_weathering"),
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
            Map.entry("doespotatotick", "doespotatotick"),
            Map.entry("cofh", "cofh_core"),
            Map.entry("xaerolib", "xaerolib"),
            Map.entry("create", "create"),
            Map.entry("cataclysm", "cataclysm"),
            Map.entry("dodosmobs", "dodosmobs"),
            Map.entry("eeeabsmobs", "eeeabsmobs"),
            Map.entry("fromtheshadows", "fromtheshadows"),
            Map.entry("gtbcs", "gtbcs_spell_lib"),
            Map.entry("gtbcsgeomancy", "gtbcs_geomancy_plus"),
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
            Map.entry("itemfilters", "itemfilters"),
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
            Map.entry("goetycataclysm", "goety_cataclysm"),
            Map.entry("fdbosses", "fdbosses"),
            Map.entry("mutantmonsters", "mutantmonsters"),
            Map.entry("cnb", "cnb"),
            Map.entry("mobgrindingutils", "mob_grinding_utils"),
            Map.entry("modularrouters", "modularrouters"),
            Map.entry("transmog", "transmog"),
            Map.entry("ironsspellbooks", "irons_spellbooks"),
            Map.entry("hostilenetworks", "hostilenetworks"),
            Map.entry("extrahnn", "extrahnn"),
            Map.entry("sanguinenetworks", "sanguine_networks"),
            Map.entry("l2archery", "l2archery"),
            Map.entry("l2library", "l2library"),
            Map.entry("l2backpack", "l2backpack"),
            Map.entry("blessfulled", "blessfulled"),
            Map.entry("tomeofblood", "tomeofblood"),
            Map.entry("betterspellcasting", "better_spellcasting"),
            Map.entry("arsadditions", "ars_additions"),
            Map.entry("adamsarsplus", "adamsarsplus"),
            Map.entry("duplicationless", "duplicationless"),
            Map.entry("fluidium", "fluidium"),
            Map.entry("corpse", "corpse"),
            Map.entry("storagedrawers", "storagedrawers"),
            Map.entry("skyarena", "skyarena"),
            Map.entry("simplyswordsoverhaul", "simply_swords_overhaul"),
            Map.entry("puffishattributes", "puffish_attributes"),
            Map.entry("toomanyglyphs", "toomanyglyphs"),
            Map.entry("tact", "tact"),
            Map.entry("dummmmmmy", "dummmmmmy"),
            Map.entry("refinedmod", "refined_mod"),
            Map.entry("farmersdelight", "farmersdelight"),
            Map.entry("sophisticatedbackpacks", "sophisticatedbackpacks"),
            Map.entry("undergarden", "undergarden"),
            Map.entry("netherexp", "netherexp"),
            Map.entry("powah", "powah"),
            Map.entry("arsnspells", "ars_n_spells"),
            Map.entry("lionfishapi", "lionfishapi"),
            Map.entry("tomeofwonders", "tome_of_wonders"),
            Map.entry("bowenchants", "bowenchants"),
            Map.entry("createaddition", "createaddition"),
            Map.entry("snuffles", "snuffles"),
            Map.entry("mekaweapons", "mekaweapons"),
            Map.entry("dungeonsdelight", "dungeonsdelight"),
            Map.entry("chimes", "chimes"),
            Map.entry("sliceanddice", "sliceanddice"),
            Map.entry("projectiledamage", "projectile_damage"),
            Map.entry("fallentraits", "fallen_traits")
    );

    private static final Map<String, Integer> GROUP_MIN_MAJOR = Map.of(
            "structurify", 2,
            "iceandfire", 2,
            "iafdragonfix", 2
    );

    private static final Map<String, String> GROUP_EXACT_VERSION = Map.of(
            "radium", "0.14.3+git.e141a47",
            "cataclysm", "3.16",
            "lionfishapi", "2.8",
            "immersiveweathering", "1.20.1-2.0.5"
    );

    private static final Map<String, String[]> MIXIN_EXACT_VERSION = Map.of(
            "create.MixinContraptionCollisionUnion", new String[]{"create", "6.0.8"},
            "geckolib.MixinAnimationControllerKeyframes", new String[]{"geckolib", "4.8.4"},
            "subtleeffects.MixinBubbleParticleTint", new String[]{"subtle_effects", "1.15.0"},
            "subtleeffects.MixinChestTickerDryChest", new String[]{"subtle_effects", "1.15.0"},
            "subtleeffects.MixinClientLevelDisplayTick", new String[]{"subtle_effects", "1.15.0"},
            "subtleeffects.MixinEntityTickerManagerRange", new String[]{"subtle_effects", "1.15.0"},
            "subtleeffects.MixinEntityTickerRange", new String[]{"subtle_effects", "1.15.0"},
            "subtleeffects.MixinLavaFluidSparks", new String[]{"subtle_effects", "1.15.0"},
            "subtleeffects.MixinWaterFluidSteam", new String[]{"subtle_effects", "1.15.0"}
    );

    private static final Map<String, String[]> GROUP_CONFLICTS = Map.of(
            "geckolib", new String[]{"gbf"},

            "iafdragonfix", new String[]{"iafdragonfix"}
    );

    private static final Map<String, String[]> MIXIN_CONFLICTS = Map.ofEntries(
            Map.entry("vanilla.MixinBeardifierReach", new String[]{"generator_accelerator"}),
            Map.entry("immersiveweathering.MixinAreaCheck", new String[]{"iwt"}),
            Map.entry("immersiveweathering.MixinBlockGrowthHandler", new String[]{"iwt"}),
            Map.entry("vanilla.MixinLevelDryRaycast", new String[]{"valkyrienskies"}),
            Map.entry("terrablender.MixinNamespacedSourceLazy", new String[]{"c2me"}),
            Map.entry("elysiumapi.MixinMultiNoiseBiomeSourceReplacers", new String[]{"terrablender"}),


            Map.entry("vanilla.MixinStructureCheck", new String[]{"modernfix"}),
            Map.entry("vanilla.MixinServerLevelStructureState", new String[]{"modernfix"}),

            Map.entry("vanilla.MixinItemEntityRenderer", new String[]{"flerovium"}),

            Map.entry("geckolib.MixinAnimationProcessorPoseHold", new String[]{"geckolib_animation_optimizer"}),

            Map.entry("vanilla.MixinChunkMapEntityTracker", new String[]{"vmp", "hariplayer"}),

            Map.entry("vanilla.MixinClientLevelBiomeBlend", new String[]{"betterbiomeblend", "betterbiomereblend"}),

            Map.entry("vanilla.MixinPoseStackAxisRotation", new String[]{"lomka"}),
            Map.entry("vanilla.MixinBlockPosBetweenClosed", new String[]{"lomka"}),
            Map.entry("vanilla.MixinPalettedContainerUniform", new String[]{"lomka"})
    );

    private static final Map<String, Set<String>> INCOMPATIBLE_MODS = Map.of(
            "soulsweaponryperformancefix", Set.of("soulsweapons.MixinWeaponUtilModPresence"),
            "ftbchunksperformancefix", Set.of("ftbchunks.MixinFTBChunksClient", "ftbchunks.MixinMapRegionImageGen",
                    "ftbchunks.MixinRenderMapImageTask"),
            "papi", Set.of("jei.MixinRecipeTransferManager"),
            "bons_and_furious", BonsAndFuriousOverlap.MIXINS
    );

    private static final String[] TAKES_EVERYTHING = {"tickweave"};

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

            Map.entry("citadel.MixinAdvancedModelBoxFastRender", new String[]{"embeddium"}),

            Map.entry("citadel.MixinBasicModelPartFastRender", new String[]{"embeddium"}),

            Map.entry("ironsspellbooks.MixinIsbCurioEquipCheck", new String[]{"curios"}),

            Map.entry("l2hostility.MixinCurioCompatHasItem", new String[]{"curios"})
    );

    private static final Map<String, String[]> MIXIN_ANY_OF = Map.of(
            "elysiumapi.MixinClimateSampler", new String[]{"elysium_api", "alexscaves"},
            "vanilla.MixinParticleEngineTextBatch", new String[]{"dummmmmmy", "blessfulled"}
    );

    private static final Map<String, Boolean> LOADED_CACHE = new HashMap<>();
    private static final Map<String, Integer> MAJOR_CACHE = new HashMap<>();
    private static final Map<String, String> VERSION_CACHE = new HashMap<>();

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
        for (String modId : TAKES_EVERYTHING) {
            if (isModPresent(modId)) {
                if (WARNED_GROUPS.add("incompatible:" + modId)) {
                    LOGGER.warn("Disabling every Collections of Optimizations patch: '{}' runs entity ticks on worker threads and its own mixins are mandatory, so none of these patches can be applied safely next to it", modId);
                }
                return false;
            }
        }
        for (Map.Entry<String, Set<String>> incompatible : INCOMPATIBLE_MODS.entrySet()) {
            if (incompatible.getValue().contains(remainder) && isModPresent(incompatible.getKey())) {
                if (WARNED_GROUPS.add("incompatible:" + incompatible.getKey())) {
                    LOGGER.warn("Disabling the Collections of Optimizations patches that overlap '{}', it patches the same code", incompatible.getKey());
                }
                return false;
            }
        }
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
            String exact = GROUP_EXACT_VERSION.get(group);
            if (modId != null && exact != null && !exact.equals(modVersion(modId))) {
                if (WARNED_GROUPS.add(group)) {
                    LOGGER.warn("Disabling '{}' optimizations: they were verified against '{}' {} but {} is installed",
                            group, modId, exact, modVersion(modId));
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
        String[] pinned = MIXIN_EXACT_VERSION.get(remainder);
        if (pinned != null && !pinned[1].equals(modVersion(pinned[0]))) {
            return false;
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
        if (ELYSIUM_SOUND_CACHE_MIXIN.equals(mixinClassName)) {
            rewireElysiumSoundHandler(targetClass);
        }
    }

    private static void rewireElysiumSoundHandler(ClassNode target) {
        String handler = null;
        for (MethodNode method : target.methods) {
            if ((method.access & Opcodes.ACC_STATIC) == 0 && ELYSIUM_HANDLER_DESC.equals(method.desc)
                    && mergedFrom(method, ELYSIUM_BLOCK_MIXIN)) {
                handler = method.name;
                method.access = (method.access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
                break;
            }
        }
        if (handler == null) {
            LOGGER.info("Collections of Optimizations: Elysium sound handler not found in {}, cache stays off", target.name);
            return;
        }
        List<MethodInsnNode> calls = new ArrayList<>();
        for (MethodNode method : target.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof MethodInsnNode call && call.owner.equals(target.name) && call.name.equals(handler)
                        && call.desc.equals(ELYSIUM_HANDLER_DESC)
                        && (call.getOpcode() == Opcodes.INVOKEVIRTUAL || call.getOpcode() == Opcodes.INVOKESPECIAL)) {
                    calls.add(call);
                }
            }
        }
        for (MethodNode method : target.methods) {
            for (MethodInsnNode call : calls) {
                if (method.instructions.contains(call)) {
                    method.instructions.set(call, new MethodInsnNode(Opcodes.INVOKESTATIC, ELYSIUM_DISPATCH_OWNER, "dispatch",
                            "(L" + target.name + ";" + ELYSIUM_HANDLER_DESC.substring(1), false));
                }
            }
        }
        LOGGER.info("Collections of Optimizations: routed {} Elysium sound handler call(s) through the cache", calls.size());
    }

    private static boolean mergedFrom(MethodNode method, String mixin) {
        return mergedFrom(method.visibleAnnotations, mixin) || mergedFrom(method.invisibleAnnotations, mixin);
    }

    private static boolean mergedFrom(List<AnnotationNode> annotations, String mixin) {
        if (annotations == null) {
            return false;
        }
        for (AnnotationNode annotation : annotations) {
            if (MIXIN_MERGED_DESC.equals(annotation.desc) && annotation.values != null) {
                for (int i = 0; i + 1 < annotation.values.size(); i += 2) {
                    if ("mixin".equals(annotation.values.get(i)) && mixin.equals(annotation.values.get(i + 1))) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        String version = modVersion(modId);
        int end = 0;
        while (end < version.length() && Character.isDigit(version.charAt(end))) {
            end++;
        }
        if (end > 0) {
            major = Integer.parseInt(version.substring(0, Math.min(end, 9)));
        }
        MAJOR_CACHE.put(modId, major);
        return major;
    }

    private static synchronized String modVersion(String modId) {
        String cached = VERSION_CACHE.get(modId);
        if (cached != null) {
            return cached;
        }
        String version = "";
        try {
            ModFileInfo modFile = LoadingModList.get().getModFileById(modId);
            if (modFile != null) {
                for (IModInfo mod : modFile.getMods()) {
                    if (mod.getModId().equals(modId)) {
                        version = mod.getVersion().toString();
                        break;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        VERSION_CACHE.put(modId, version);
        return version;
    }
}
