package com.misanthropy.collections_of_optimizations.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Map.entry("mekanism", "mekanism"),
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
            Map.entry("goetyawaken", "goetyawaken"),
            Map.entry("almostunified", "almostunified"),
            Map.entry("kubejs", "kubejs"),
            Map.entry("ldlib", "ldlib"),
            Map.entry("gnetum", "gnetum"),
            Map.entry("fwa", "fwa"),
            Map.entry("tooltipoverhaul", "tooltipoverhaul"),
            Map.entry("cablefacades", "cable_facades"),
            Map.entry("structureessentials", "structureessentials"),
            Map.entry("oculus", "oculus"),
            Map.entry("citadel", "citadel"),
            Map.entry("asyncparticles", "asyncparticles"),
            Map.entry("naturesaura", "naturesaura"),
            Map.entry("xaerominimap", "xaerominimap"),
            Map.entry("w2w2", "w2w2"),
            Map.entry("terrablender", "terrablender"),
            Map.entry("immersiveengineering", "immersiveengineering"),
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
            Map.entry("ftblibrary", "ftblibrary"),
            Map.entry("jei", "jei"),
            Map.entry("ftbxmodcompat", "ftbxmodcompat"),
            Map.entry("structurify", "structurify"),
            Map.entry("bloodmagic", "bloodmagic"),
            Map.entry("animus", "animus"),
            Map.entry("patchouli", "patchouli"),
            Map.entry("placebo", "placebo"),
            Map.entry("punchy", "punchy"),
            Map.entry("embeddium", "embeddium"),
            Map.entry("flerovium", "flerovium"),
            Map.entry("alltheleaks", "alltheleaks"),
            Map.entry("l2hostility", "l2hostility"),
            Map.entry("l2complements", "l2complements"),
            Map.entry("integratedapi", "integrated_api"),
            Map.entry("elysiumapi", "elysium_api"),
            Map.entry("cerbonsapi", "cerbons_api"),
            Map.entry("echelon", "tiered"),
            Map.entry("balm", "balm"),
            Map.entry("biggerstacks", "biggerstacks"),
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
            Map.entry("compositematerial", "composite_material"),
            Map.entry("moonlight", "moonlight"),
            Map.entry("rpl", "ritchiesprojectilelib"),
            Map.entry("cbc", "createbigcannons"),
            Map.entry("witherreincarnated", "witherreincarnated"),
            Map.entry("roacw", "roacw"),
            Map.entry("subtleeffects", "subtle_effects"),
            Map.entry("touchnmotion", "touch_n_motion"),
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
            Map.entry("flywheel", "flywheel"),
            Map.entry("cataclysm", "cataclysm"),
            Map.entry("dodosmobs", "dodosmobs"),
            Map.entry("eeeabsmobs", "eeeabsmobs"),
            Map.entry("fromtheshadows", "fromtheshadows"),
            Map.entry("gtbcs", "gtbcs_spell_lib"),
            Map.entry("gtbcsgeomancy", "gtbcs_geomancy_plus"),
            Map.entry("legendarymonsters", "legendary_monsters"),
            Map.entry("xaeroworldmap", "xaeroworldmap"),
            Map.entry("byepregen", "byepregen"),
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
            Map.entry("jerotes", "jerotes"),
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
            Map.entry("immersiveoptimization", "immersive_optimization"),
            Map.entry("chunksending", "chunksending"),
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
            Map.entry("fallentraits", "fallen_traits"),
            Map.entry("timm", "timm"),
            Map.entry("mgnflowers", "mgnflowers")
    );

    private static final Map<String, Integer> GROUP_MIN_MAJOR = Map.of(
            "structurify", 2,
            "iceandfire", 2,
            "iafdragonfix", 2
    );

    private static final Map<String, String> GROUP_EXACT_VERSION = Map.of(
            "radium", "0.14.4+git.53d76d5",
            "cataclysm", "3.16,3.31",
            "lionfishapi", "2.8,3.0",
            "immersiveweathering", "1.20.1-2.0.5",
            "timm", "1.1.0-Forge",
            "touchnmotion", "1.0.4",
            "witherreincarnated", "1.0.5",
            "roacw", "1.2.4"
    );

    private static final Map<String, String> GROUP_VERIFIED_VERSION = Map.ofEntries(
            Map.entry("radium", "0.15.0+git.626588c"),
            Map.entry("mekanism", "10.4.16"),
            Map.entry("curios", "5.14.1+1.20.1"),
            Map.entry("oddaccessories", "1.1.6"),
            Map.entry("artifacts", "9.5.19"),
            Map.entry("caelus", "3.2.0+1.20.1"),
            Map.entry("lootr", "0.7.35.94"),
            Map.entry("justdirethings", "1.2.0"),
            Map.entry("dungeoncrawl", "2.3.15"),
            Map.entry("goety", "2.5.60.2"),
            Map.entry("shouldersurfing", "1.20.1-5.0.11"),
            Map.entry("mythsandlegends", "0.0.8.8"),
            Map.entry("apothicattributes", "1.3.7"),
            Map.entry("arsnouveau", "4.12.7"),
            Map.entry("arselemental", "0.6.8.0"),
            Map.entry("goetydelight", "1.5.3"),
            Map.entry("endinglibrary", "2.2.1fix"),
            Map.entry("goetyrevelation", "2.3.5"),
            Map.entry("geckolib", "4.8.4"),
            Map.entry("goetyawaken", "26.11.1"),
            Map.entry("almostunified", "1.20.1-0.11.0"),
            Map.entry("kubejs", "2001.6.5-build.26"),
            Map.entry("gnetum", "2.5.2+1.20.1-forge"),
            Map.entry("fwa", "1.3.9"),
            Map.entry("tooltipoverhaul", "2.0.0"),
            Map.entry("cablefacades", "2.1.3"),
            Map.entry("structureessentials", "1.20.1-5.0"),
            Map.entry("oculus", "1.8.11"),
            Map.entry("citadel", "2.6.3"),
            Map.entry("asyncparticles", "20.1.4.7"),
            Map.entry("naturesaura", "39.4"),
            Map.entry("xaerominimap", "26.5.0"),
            Map.entry("terrablender", "3.0.1.10"),
            Map.entry("immersiveengineering", "1.20.1-10.2.0-183"),
            Map.entry("terramity", "0.9.8"),
            Map.entry("armageddon", "3.3.0"),
            Map.entry("borninchaos", "1.7.5"),
            Map.entry("brutalbosses", "1.20.1-8.5"),
            Map.entry("brutality", "0.8.5-1.20.1"),
            Map.entry("bossesrise", "1.0.9.2."),
            Map.entry("soulsweapons", "1.4.8-1.20.1-forge}"),
            Map.entry("konweapon", "0.1.5"),
            Map.entry("immersiveaircraft", "1.5.0+1.20.1"),
            Map.entry("ftbchunks", "2001.3.8"),
            Map.entry("jei", "15.74.0.227"),
            Map.entry("ftbxmodcompat", "2.1.3"),
            Map.entry("bloodmagic", "3.3.8-50"),
            Map.entry("animus", "3.0.35"),
            Map.entry("patchouli", "1.20.1-85-FORGE"),
            Map.entry("placebo", "8.6.3"),
            Map.entry("punchy", "3.2b"),
            Map.entry("embeddium", "0.3.32-beta.120-dirty+mc1.20.1"),
            Map.entry("flerovium", "1.2.19"),
            Map.entry("alltheleaks", "1.1.6+1.20.1-forge"),
            Map.entry("l2hostility", "2.5.19"),
            Map.entry("l2complements", "2.6.1"),
            Map.entry("integratedapi", "1.7.4+1.20.1-forge"),
            Map.entry("elysiumapi", "1.1.3"),
            Map.entry("cerbonsapi", "1.0.0"),
            Map.entry("balm", "7.3.44"),
            Map.entry("biggerstacks", "1.20.1-2026.06.17"),
            Map.entry("macabre", "0.8.4"),
            Map.entry("alexsmobs", "1.22.9"),
            Map.entry("alexscaves", "2.0.2"),
            Map.entry("architectury", "9.2.14"),
            Map.entry("adastra", "1.15.21"),
            Map.entry("zeta", "1.0-31"),
            Map.entry("quark", "4.0-462"),
            Map.entry("perception", "0.1.4"),
            Map.entry("pehkui", "3.8.2+1.20.1-forge"),
            Map.entry("relics", "0.8.0.13"),
            Map.entry("morerelics", "1.7.7"),
            Map.entry("terracurio", "0.1.13"),
            Map.entry("cosmeticarmor", "1.20.1-v1a"),
            Map.entry("morehitboxes", "1.9.2"),
            Map.entry("compositematerial", "1.3.0"),
            Map.entry("moonlight", "1.20-2.16.39"),
            Map.entry("witherreincarnated", "1.0.5"),
            Map.entry("roacw", "1.2.4"),
            Map.entry("subtleeffects", "1.15.1-hotfix.1"),
            Map.entry("touchnmotion", "1.0.4"),
            Map.entry("immersiveweathering", "1.20.1-2.0.5"),
            Map.entry("tonsofenchants", "1.0.0"),
            Map.entry("arseng", "1.2.0"),
            Map.entry("saintsdragons", "0.9.88"),
            Map.entry("emf", "3.3.12"),
            Map.entry("etf", "7.2.6"),
            Map.entry("mowziesmobs", "1.8.2"),
            Map.entry("pickupnotifier", "8.0.0"),
            Map.entry("photon", "1.1.17"),
            Map.entry("immediatelyfast", "1.5.5+1.20.4"),
            Map.entry("modernfix", "5.27.92+mc1.20.1"),
            Map.entry("fancymenu", "3.10.0"),
            Map.entry("ironslib", "1.20.1-2.3.0"),
            Map.entry("bettercombat", "1.9.0+1.20.1"),
            Map.entry("doespotatotick", "1.20.1-5.6.3"),
            Map.entry("cofh", "11.0.2"),
            Map.entry("create", "6.0.8"),
            Map.entry("cataclysm", "3.31"),
            Map.entry("eeeabsmobs", "1.20.1-0.98.1"),
            Map.entry("fromtheshadows", "2.9"),
            Map.entry("gtbcs", "2.2.0-1.20.1"),
            Map.entry("gtbcsgeomancy", "2.0.0-1.20.1"),
            Map.entry("xaeroworldmap", "1.46.0"),
            Map.entry("byepregen", "1.1.3.3"),
            Map.entry("supplementaries", "1.20-3.1.45"),
            Map.entry("amendments", "1.20-2.2.6"),
            Map.entry("copycats", "3.0.10+mc.1.20.1-forge"),
            Map.entry("biomeswevegone", "1.8.2"),
            Map.entry("industrialforegoing", "3.5.22"),
            Map.entry("enigmaticaddons", "1.2.6"),
            Map.entry("enigmaticdelicacy", "1.0.1"),
            Map.entry("enigmaticlegacy", "2.30.1"),
            Map.entry("traveloptics", "7.2.0-1.20.1"),
            Map.entry("celestialcore", "1.6.1"),
            Map.entry("celestialenchantments", "1.3.7"),
            Map.entry("uniqueaccessories", "2.0.1"),
            Map.entry("summonity", "0.1.7.1"),
            Map.entry("crittersandcompanions", "1.20.1-2.3.5"),
            Map.entry("cucumber", "7.0.16"),
            Map.entry("mysticalagriculture", "7.0.24"),
            Map.entry("konkrete", "1.8.0"),
            Map.entry("titanium", "3.8.37"),
            Map.entry("domesticationinnovation", "1.7.1"),
            Map.entry("moreartifacts", "1.5.5"),
            Map.entry("simplymore", "1.1.4"),
            Map.entry("simplyswords", "1.70.2-1.20.1"),
            Map.entry("celestisynth", "1.20.1-1.3.4"),
            Map.entry("covenantoftheseven", "2.2.6-hotfix"),
            Map.entry("hexerei", "1.20.1-0.4.2.3"),
            Map.entry("easyvillagers", "1.20.1-1.1.23"),
            Map.entry("manybows", "4.2.3"),
            Map.entry("immersivearmors", "1.7.2+1.20.1"),
            Map.entry("immersiveoptimization", "0.2.0"),
            Map.entry("chunksending", "1.20.1-4.1"),
            Map.entry("starcatcher", "3.1.4.6-FORGE-1.20.1"),
            Map.entry("textanimator", "2.5.4"),
            Map.entry("itemfilters", "2001.1.0-build.59"),
            Map.entry("cognition", "2.4.12-1.20.1"),
            Map.entry("opposingforce", "1.20.1-2.0.3"),
            Map.entry("peyroscythe", "1.20.1-1.3.3"),
            Map.entry("somakespells", "1.0.9"),
            Map.entry("notenoughanimations", "1.12.7"),
            Map.entry("lethality", "0.3-1.20.1"),
            Map.entry("castercuriosbonus", "1.20.1-1.1.1"),
            Map.entry("legendaryspellbooks", "0.4.1"),
            Map.entry("meetyourfight", "1.20.1-1.6.2"),
            Map.entry("bmd", "1.1.1"),
            Map.entry("mahoutsukai", "1.20.1-v1.34.81"),
            Map.entry("primitivemobs", "1.0.4"),
            Map.entry("ribbits", "1.20.1-Forge-3.0.5"),
            Map.entry("hazennstuff", "1.1.2"),
            Map.entry("species", "4.0-alpha"),
            Map.entry("cookingforblockheads", "16.0.15"),
            Map.entry("deeperdarker", "1.3.3"),
            Map.entry("goetyhostility", "1.4.4"),
            Map.entry("goetycataclysm", "1.20-1.9.3"),
            Map.entry("fdbosses", "3.1.0.3"),
            Map.entry("mutantmonsters", "8.0.8"),
            Map.entry("cnb", "1.7.10"),
            Map.entry("mobgrindingutils", "1.20.1-1.1.0"),
            Map.entry("modularrouters", "12.1.1+mc1.20.1"),
            Map.entry("transmog", "1.3.0+1.20"),
            Map.entry("ironsspellbooks", "1.20.1-3.16.3"),
            Map.entry("hostilenetworks", "5.3.3"),
            Map.entry("extrahnn", "1.2.2"),
            Map.entry("sanguinenetworks", "2.0.1"),
            Map.entry("l2archery", "2.5.0"),
            Map.entry("l2library", "2.5.5"),
            Map.entry("blessfulled", "0.6"),
            Map.entry("tomeofblood", "0.4.5"),
            Map.entry("betterspellcasting", "1.0.0"),
            Map.entry("arsadditions", "1.20.1-1.6.7"),
            Map.entry("adamsarsplus", "1.20.1-5.0.3"),
            Map.entry("duplicationless", "1.20.1-1.2.1"),
            Map.entry("fluidium", "1.20.1-1.4.0"),
            Map.entry("corpse", "1.20.1-1.0.23"),
            Map.entry("skyarena", "1.3.6-1.20.1"),
            Map.entry("puffishattributes", "0.8.2"),
            Map.entry("tact", "1.6.0"),
            Map.entry("dummmmmmy", "1.20-2.0.11"),
            Map.entry("refinedmod", "1.20.1-1.1.6"),
            Map.entry("farmersdelight", "1.20.1-1.2.11a"),
            Map.entry("sophisticatedbackpacks", "3.26.22.2264"),
            Map.entry("undergarden", "0.8.14"),
            Map.entry("netherexp", "2.3.5"),
            Map.entry("arsnspells", "3.2.0"),
            Map.entry("lionfishapi", "3.0"),
            Map.entry("bowenchants", "1.2.0"),
            Map.entry("createaddition", "1.20.1-1.3.3"),
            Map.entry("snuffles", "1.2.0.1"),
            Map.entry("mekaweapons", "4.2.1"),
            Map.entry("dungeonsdelight", "1.2.10"),
            Map.entry("chimes", "2.1.1"),
            Map.entry("sliceanddice", "3.6.0"),
            Map.entry("projectiledamage", "3.2.2+1.20.1"),
            Map.entry("fallentraits", "1.20.1-1.0.1"),
            Map.entry("timm", "1.1.0-Forge"),
            Map.entry("mgnflowers", "2.0.0")
    );

    private static final Map<String, String[]> MIXIN_MIN_VERSION = Map.of(
            "fancymenu.MixinGuiGraphicsPoseStack", new String[]{"fancymenu", "3.9.0"},
            "create.ContraptionClientAccessor", new String[]{"create", "6.0.7"},
            "create.MixinContraptionEntityRenderLevelRelease", new String[]{"create", "6.0.7"},
            "fwa.MixinFwaAnimationsLookup", new String[]{"fwa", "1.3.8"},
            "species.MixinSpeciesDisguiseCheck", new String[]{"species", "4.0-alpha"}
    );

    private static final Map<String, String[]> MIXIN_EXACT_VERSION = Map.ofEntries(
            Map.entry("create.MixinContraptionCollisionUnion", new String[]{"create", "6.0.8"}),
            Map.entry("species.MixinSpeciesRenderListener", new String[]{"species", "3.5"}),
            Map.entry("species.MixinSpeciesLimpetScareScan", new String[]{"species", "3.5"}),
            Map.entry("cbc.MixinCbcAutocannonSeat", new String[]{"createbigcannons", "5.11.4"}),
            Map.entry("goety.MixinGoetyGlowGate", new String[]{"goety", "2.5.60.2"}),
            Map.entry("goetydelight.MixinEntityVisualEffects", new String[]{"goetydelight", "1.5.0fix4"}),
            Map.entry("goetydelight.MixinEntityVisualEffectRenderDispatcher", new String[]{"goetydelight", "1.5.0fix4"}),
            Map.entry("goetydelight.MixinScreenSpaceDepthEffectPostProcessor", new String[]{"goetydelight", "1.5.0fix4"}),
            Map.entry("goetydelight.MixinDisplaySkinBytes", new String[]{"goetydelight", "1.5.3"}),
            Map.entry("cataclysm.MixinCataclysmMonstrosityBlockBreaking", new String[]{"cataclysm", "3.16"}),
            Map.entry("geckolib.MixinAnimationControllerKeyframes", new String[]{"geckolib", "4.8.4"}),
            Map.entry("geckolib.MixinBakedAnimationsAdapterShare", new String[]{"geckolib", "4.8.4"}),
            Map.entry("quark.MixinItemStackCapturedAttributes", new String[]{"quark", "4.0-462"}),
            Map.entry("subtleeffects.MixinBubbleParticleTint", new String[]{"subtle_effects", "1.15.0", "1.15.1-hotfix.1"}),
            Map.entry("subtleeffects.MixinChestTickerDryChest", new String[]{"subtle_effects", "1.15.0", "1.15.1-hotfix.1"}),
            Map.entry("subtleeffects.MixinClientLevelDisplayTick", new String[]{"subtle_effects", "1.15.0", "1.15.1-hotfix.1"}),
            Map.entry("subtleeffects.MixinEntityTickerManagerRange", new String[]{"subtle_effects", "1.15.0", "1.15.1-hotfix.1"}),
            Map.entry("subtleeffects.MixinEntityTickerRange", new String[]{"subtle_effects", "1.15.0", "1.15.1-hotfix.1"}),
            Map.entry("subtleeffects.MixinLavaFluidSparks", new String[]{"subtle_effects", "1.15.0", "1.15.1-hotfix.1"}),
            Map.entry("subtleeffects.MixinWaterFluidSteam", new String[]{"subtle_effects", "1.15.0", "1.15.1-hotfix.1"}),
            Map.entry("alexscaves.MixinACSkipUnlistedDimensions", new String[]{"alexscaves", "2.0.2"}),
            Map.entry("ftblibrary.MixinSidebarPreparedReads", new String[]{"ftblibrary", "2001.2.13"})
    );

    private static final Map<String, String[]> GROUP_CONFLICTS = Map.of(
            "geckolib", new String[]{"gbf"},

            "iafdragonfix", new String[]{"iafdragonfix"}
    );

    private static final Map<String, String[]> MIXIN_CONFLICTS = Map.ofEntries(
            Map.entry("vanilla.MixinBeardifierReach", new String[]{"generator_accelerator"}),
            Map.entry("vanilla.MixinRenderTargetLeakFree", new String[]{"gpumemleakfix"}),
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
            Map.entry("vanilla.MixinModelPartRotationReuse", new String[]{"sleepium"}),
            Map.entry("vanilla.MixinBlockPosBetweenClosed", new String[]{"lomka"}),
            Map.entry("vanilla.MixinPalettedContainerUniform", new String[]{"lomka"}),
            Map.entry("citadel.MixinCitadelTrackedBroadcast", new String[]{"alexsmobsandcavesoptimization"}),
            Map.entry("alexsmobs.MixinAlexsMobsTrackedBroadcast", new String[]{"alexsmobsandcavesoptimization"}),
            Map.entry("alexscaves.MixinAlexsCavesTrackedBroadcast", new String[]{"alexsmobsandcavesoptimization"}),
            Map.entry("citadel.MixinPathfindingBoundedQueue", new String[]{"alexsmobsandcavesoptimization"}),
            Map.entry("citadel.MixinPathResultBoundedQueue", new String[]{"alexsmobsandcavesoptimization"}),
            Map.entry("alexscaves.MixinACScareScanReuse", new String[]{"alexsmobsandcavesoptimization"})
    );

    private static final Map<String, Set<String>> INCOMPATIBLE_MODS = Map.of(
            "soulsweaponryperformancefix", Set.of("soulsweapons.MixinWeaponUtilModPresence"),
            "ftbchunksperformancefix", Set.of("ftbchunks.MixinFTBChunksClient", "ftbchunks.MixinMapRegionImageGen",
                    "ftbchunks.MixinRenderMapImageTask"),
            "papi", Set.of("jei.MixinRecipeTransferManager"),
            "quark", Set.of("apothicattributes.MixinApothReplacedTooltipModifiers"),
            "bons_and_furious", BonsAndFuriousOverlap.MIXINS
    );

    private static final Map<String, String> TAKES_EVERYTHING = Map.of(
            "tickweave", "runs entity ticks on worker threads and its own mixins are mandatory",
            "vulkanmod", "replaces the whole OpenGL renderer and overwrites the methods these patches target"
    );

    private static final Map<String, String[]> MIXIN_REQUIRES = Map.ofEntries(
            Map.entry("elysiumapi.MixinMultiNoiseBiomeSourceTerrablender", new String[]{"terrablender"}),

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
            Map.entry("emf.MixinEmfModelPartFastCompile", new String[]{"embeddium"}),
            Map.entry("emf.MixinEmfCubeVertices", new String[]{"embeddium"}),

            Map.entry("fwa.MixinFwaFastQuads", new String[]{"embeddium"}),

            Map.entry("vanilla.MixinLevelRendererMeshReuse", new String[]{"embeddium"}),
            Map.entry("vanilla.MixinEntityMeshHolder", new String[]{"embeddium"}),
            Map.entry("geckolib.MixinGeoArmorRendererMeshReuse", new String[]{"embeddium"}),
            Map.entry("vanilla.MixinVertexBufferMeshReuse", new String[]{"embeddium"}),
            Map.entry("vanilla.MixinRenderTypeMeshReuse", new String[]{"embeddium"}),
            Map.entry("vanilla.MixinBlockEntityMeshHolder", new String[]{"embeddium"}),
            Map.entry("vanilla.MixinBlockEntityRenderDispatcherMeshReuse", new String[]{"embeddium"}),
            Map.entry("vanilla.MixinRenderLayerCutoutReplay", new String[]{"embeddium"}),
            Map.entry("vanilla.MixinVillagerLayerModelReplay", new String[]{"embeddium"}),
            Map.entry("geckolib.MixinGeoRenderer", new String[]{"embeddium"}),
            Map.entry("lionfishapi.MixinLionfishModelBoxRender", new String[]{"embeddium"}),


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
    private static final Map<String, Boolean> GROUP_FITS = new HashMap<>();
    private static final Map<String, List<String>> GROUP_MIXINS = new HashMap<>();
    private static final Pattern VERSION_LINE = Pattern.compile("^(\\d+)(?:\\.(\\d+))?");
    private static MixinFitCheck fitCheck;

    @Override
    public void onLoad(String mixinPackage) {
        if (!isModPresent("mcreator_mem_fix")) {
            McreatorVariableTransformer.install();
        }
        loadGroupMixins();
    }

    private static void loadGroupMixins() {
        try (InputStream in = CoOMixinPlugin.class.getClassLoader().getResourceAsStream("collections_of_optimizations.mixins.json")) {
            if (in == null) {
                return;
            }
            JsonObject config = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            List<String> sections = new ArrayList<>(List.of("mixins"));
            sections.add(FMLLoader.getDist().isClient() ? "client" : "server");
            for (String section : sections) {
                JsonArray names = config.getAsJsonArray(section);
                if (names == null) {
                    continue;
                }
                for (JsonElement name : names) {
                    String mixin = name.getAsString();
                    int separator = mixin.indexOf('.');
                    if (separator > 0) {
                        GROUP_MIXINS.computeIfAbsent(mixin.substring(0, separator), group -> new ArrayList<>()).add(mixin);
                    }
                }
            }
        } catch (Throwable throwable) {
            LOGGER.warn("Collections of Optimizations could not read its mixin list, patches will not be checked against installed mod versions", throwable);
        }
    }

    private static synchronized boolean groupFits(String group) {
        String modId = GROUP_TO_MODID.get(group);
        if (modId == null) {
            return true;
        }
        Boolean cached = GROUP_FITS.get(group);
        if (cached != null) {
            return cached;
        }
        boolean fits = true;
        String installed = modVersion(modId);
        String verified = GROUP_VERIFIED_VERSION.get(group);
        if (verified != null && isNewerVersionLine(installed, verified)) {
            LOGGER.warn("Disabling '{}' optimizations: they were made for '{}' {} and {} is a newer major version, the game keeps running without them",
                    group, modId, verified, installed);
            fits = false;
        } else {
            if (fitCheck == null) {
                fitCheck = new MixinFitCheck("collections_of_optimizations.refmap.json");
            }
            for (String mixin : GROUP_MIXINS.getOrDefault(group, List.of())) {
                if (!passesGates(mixin, group)) {
                    continue;
                }
                String problem;
                try {
                    problem = fitCheck.mismatch(MIXIN_PACKAGE + mixin);
                } catch (Throwable throwable) {
                    problem = null;
                }
                if (problem != null) {
                    LOGGER.warn("Disabling '{}' optimizations: '{}' {} changed since they were made ({}: {}), the game keeps running without them",
                            group, modId, installed, mixin, problem);
                    fits = false;
                    break;
                }
            }
        }
        GROUP_FITS.put(group, fits);
        return fits;
    }

    private static boolean isNewerVersionLine(String installed, String verified) {
        int[] installedLine = versionLine(installed);
        int[] verifiedLine = versionLine(verified);
        if (installedLine == null || verifiedLine == null) {
            return false;
        }
        if (installedLine[0] != verifiedLine[0]) {
            return installedLine[0] > verifiedLine[0];
        }
        return verifiedLine[0] == 0 && installedLine[1] > verifiedLine[1];
    }

    private static int[] versionLine(String version) {
        String trimmed = version.replaceFirst("(?i)^(mc|forge-|neoforge-)", "")
                .replaceFirst("(?i)^(mc)?1\\.20(\\.1)?[-+_]", "")
                .replaceFirst("(?i)^(forge|neoforge)[-_]", "")
                .replaceFirst("(?i)^v", "");
        if (trimmed.equals(version) && version.matches("1\\.20(\\.1)?")) {
            return null;
        }
        Matcher matcher = VERSION_LINE.matcher(trimmed);
        if (!matcher.find()) {
            return null;
        }
        try {
            int major = Integer.parseInt(matcher.group(1));
            int minor = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
            return new int[]{major, minor};
        } catch (NumberFormatException exception) {
            return null;
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
        return passesGates(remainder, group) && groupFits(group);
    }

    private static boolean passesGates(String remainder, String group) {
        // totally not a code comment to commit again
        for (Map.Entry<String, String> takesEverything : TAKES_EVERYTHING.entrySet()) {
            String modId = takesEverything.getKey();
            if (isModPresent(modId)) {
                if (WARNED_GROUPS.add("incompatible:" + modId)) {
                    LOGGER.warn("Disabling every Collections of Optimizations patch: '{}' {}, so none of these patches can be applied safely next to it", modId, takesEverything.getValue());
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
            if (modId != null && exact != null && !List.of(exact.split(",")).contains(modVersion(modId))) {
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
        if (pinned != null && !Arrays.asList(pinned).subList(1, pinned.length).contains(modVersion(pinned[0]))) {
            if (WARNED_GROUPS.add("pinned:" + remainder)) {
                LOGGER.warn("Skipping '{}': built for '{}' {}, installed {}", remainder, pinned[0], String.join(" or ", Arrays.asList(pinned).subList(1, pinned.length)), modVersion(pinned[0]));
            }
            return false;
        }
        String[] minimum = MIXIN_MIN_VERSION.get(remainder);
        if (minimum != null && new DefaultArtifactVersion(modVersion(minimum[0])).compareTo(new DefaultArtifactVersion(minimum[1])) < 0) {
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
        fitCheck = null;
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
        if (mixinClassName.endsWith("HugeMethods") && !"false".equals(System.getProperty("coo.splitHugeMethods"))) {
            HugeMethodSplitter.split(targetClass);
        }
        if (mixinClassName.endsWith("MixinAtomicBitSetExactVarHandle")) {
            exactVarHandleWrites(targetClass);
        }
    }

    private static void exactVarHandleWrites(ClassNode target) {
        for (MethodNode method : target.methods) {
            boolean changed = false;
            for (org.objectweb.asm.tree.AbstractInsnNode insn : method.instructions) {
                if (insn instanceof org.objectweb.asm.tree.MethodInsnNode call && call.owner.equals("java/lang/invoke/VarHandle")
                        && call.name.equals("setRelease") && call.desc.equals("([JII)V")
                        && call.getPrevious() != null && call.getPrevious().getOpcode() == Opcodes.ICONST_0) {
                    method.instructions.set(call.getPrevious(), new org.objectweb.asm.tree.InsnNode(Opcodes.LCONST_0));
                    call.desc = "([JIJ)V";
                    changed = true;
                }
            }
            if (changed) {
                method.maxStack += 1;
            }
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
