package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WRWorld;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PlacedFeatureData extends DatapackBuiltinEntriesProvider {
    public PlacedFeatureData(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, new RegistrySetBuilder().add(Registries.PLACED_FEATURE, PlacedFeatureData::bootstrap), Set.of(Wyrmroost.MOD_ID));
    }

    private static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(
                WRWorld.PlacedFeatures.PLATINUM_ORE,
                new PlacedFeature(
                        configuredFeatures.getOrThrow(WRWorld.ConfiguredFeatures.PLATINUM_ORE),
                        List.of(
                                HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(64))),
                                CountPlacement.of(20)
                        )
                )
        );
        context.register(
                WRWorld.PlacedFeatures.BLUE_GEODE_ORE,
                new PlacedFeature(
                        configuredFeatures.getOrThrow(WRWorld.ConfiguredFeatures.BLUE_GEODE_ORE),
                        List.of(
                                HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(16))),
                                CountPlacement.of(1)
                        )
                )
        );

        context.register(
                WRWorld.PlacedFeatures.RED_GEODE_ORE,
                new PlacedFeature(
                        configuredFeatures.getOrThrow(WRWorld.ConfiguredFeatures.RED_GEODE_ORE),
                        List.of(
                                HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(128))),
                                CountPlacement.of(8)
                        )
                )
        );

        context.register(
                WRWorld.PlacedFeatures.PURPLE_GEODE_ORE,
                new PlacedFeature(
                        configuredFeatures.getOrThrow(WRWorld.ConfiguredFeatures.RED_GEODE_ORE),
                        List.of(
                                HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80))),
                                CountPlacement.of(45)
                        )
                )
        );
    }

    @Override
    public String getName() {
        return "Wyrmroost Placed Feaures";
    }
}
