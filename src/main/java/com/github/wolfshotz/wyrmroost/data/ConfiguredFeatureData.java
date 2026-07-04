package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.registry.WRWorld;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ConfiguredFeatureData extends DatapackBuiltinEntriesProvider {


    public ConfiguredFeatureData(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, new RegistrySetBuilder().add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureData::bootstrap), Set.of(Wyrmroost.MOD_ID));
    }

    private static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(
                WRWorld.ConfiguredFeatures.PLATINUM_ORE,
                new ConfiguredFeature<>(
                        Feature.ORE,
                        new OreConfiguration(
                                List.of(
                                        OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), WRBlocks.PLATINUM_ORE.value().defaultBlockState())
                                ),
                                10
                        )
                )
        );

        context.register(
                WRWorld.ConfiguredFeatures.BLUE_GEODE_ORE,
                new ConfiguredFeature<>(
                        Feature.ORE,
                        new OreConfiguration(
                                List.of(OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), WRBlocks.BLUE_GEODE_ORE.value().defaultBlockState())),
                                9
                        )
                )
        );

        context.register(
                WRWorld.ConfiguredFeatures.RED_GEODE_ORE,
                new ConfiguredFeature<>(
                        Feature.ORE,
                        new OreConfiguration(
                                List.of(
                                        OreConfiguration.target(new TagMatchTest(Tags.Blocks.ORES_IN_GROUND_NETHERRACK), WRBlocks.RED_GEODE_ORE.value().defaultBlockState())
                                ),
                                4
                        )
                )
        );

        context.register(
                WRWorld.ConfiguredFeatures.PURPLE_GEODE_ORE,
                new ConfiguredFeature<>(
                        Feature.ORE,
                        new OreConfiguration(
                                List.of(
                                        OreConfiguration.target(new TagMatchTest(Tags.Blocks.END_STONES), WRBlocks.PURPLE_GEODE_ORE.value().defaultBlockState())
                                ),
                                4,
                                1
                        )
                )
        );
    }

    @Override
    public String getName() {
        return "Wyrmroost Configured Feaures";
    }
}
