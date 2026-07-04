package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRWorld;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

// Honestly, I like Neo's way of making injections in stuff to existing biomes more than Fabric's
// One of things they did actually correctly. And the rest is a pile of unmaintained garbage - Nord
public class BiomeModifiersData extends DatapackBuiltinEntriesProvider {

    public BiomeModifiersData(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, new RegistrySetBuilder().add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifiersData::bootstrap), Set.of(Wyrmroost.MOD_ID));
    }

    private static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("alpine_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_MOUNTAIN_PEAK),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.ALPINE.value(),
                                1, 4, 2
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("butterfly_leviathan_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_OCEAN),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.BUTTERFLY_LEVIATHAN.value(),
                                1, 1, 1
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("canari_wyvern_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_SWAMP),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.CANARI_WYVERN.value(),
                                2, 5, 9
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("dragon_fruit_drake_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_JUNGLE),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.DRAGON_FRUIT_DRAKE.value(),
                                4, 5, 23
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("lesser_desert_wyrm_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_DESERT),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.LESSER_DESERTWYRM.value(),
                                1, 3, 13
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("overworld_drake_savanna_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_SAVANNA),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.OVERWORLD_DRAKE.value(),
                                1, 3, 8
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("overworld_drake_plains_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.OVERWORLD_DRAKE.value(),
                                1, 3, 8
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("roost_stalker_plains_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.ROOSTSTALKER.value(),
                                2, 9, 7
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("roost_stalker_forest_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_FOREST),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.ROOSTSTALKER.value(),
                                2, 9, 7
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("roost_stalker_mountain_slope_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_MOUNTAIN_SLOPE),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.ROOSTSTALKER.value(),
                                2, 9, 7
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("royal_red_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_MOUNTAIN_PEAK),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.ROYAL_RED.value(),
                                1, 1, 1
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("silver_glider_ocean_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_OCEAN),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.SILVER_GLIDER.value(),
                                1, 4, 10
                        )
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("silver_glider_beach_spawn")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        biomes.getOrThrow(Tags.Biomes.IS_BEACH),
                        new MobSpawnSettings.SpawnerData(
                                WREntities.SILVER_GLIDER.value(),
                                1, 4, 10
                        )
                )
        );


        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("platinum_ore")),
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(Tags.Biomes.IS_OVERWORLD),
                        HolderSet.direct(features.getOrThrow(WRWorld.PlacedFeatures.PLATINUM_ORE)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("blue_geode_ore")),
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(Tags.Biomes.IS_OVERWORLD),
                        HolderSet.direct(features.getOrThrow(WRWorld.PlacedFeatures.BLUE_GEODE_ORE)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("red_geode_ore")),
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(Tags.Biomes.IS_NETHER),
                        HolderSet.direct(features.getOrThrow(WRWorld.PlacedFeatures.RED_GEODE_ORE)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Wyrmroost.rl("purple_geode_ore")),
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(Tags.Biomes.IS_END),
                        HolderSet.direct(features.getOrThrow(WRWorld.PlacedFeatures.PURPLE_GEODE_ORE)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
    }


    @Override
    public String getName() {
        return "Wyrmroost Biome Modifiers";
    }
}
