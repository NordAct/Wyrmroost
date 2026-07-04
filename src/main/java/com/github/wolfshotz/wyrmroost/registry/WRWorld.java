package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WRWorld {
    public static class PlacedFeatures {
        public static final ResourceKey<PlacedFeature> PLATINUM_ORE = ResourceKey.create(Registries.PLACED_FEATURE, Wyrmroost.rl("platinum_ore"));
        public static final ResourceKey<PlacedFeature> BLUE_GEODE_ORE = ResourceKey.create(Registries.PLACED_FEATURE, Wyrmroost.rl("blue_geode_ore"));
        public static final ResourceKey<PlacedFeature> RED_GEODE_ORE = ResourceKey.create(Registries.PLACED_FEATURE, Wyrmroost.rl("reg_geode_ore"));
        public static final ResourceKey<PlacedFeature> PURPLE_GEODE_ORE = ResourceKey.create(Registries.PLACED_FEATURE, Wyrmroost.rl("purple_geode_ore"));
    }

    public static class ConfiguredFeatures {
        public static final ResourceKey<ConfiguredFeature<?,?>> PLATINUM_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, PlacedFeatures.PLATINUM_ORE.location());
        public static final ResourceKey<ConfiguredFeature<?,?>> BLUE_GEODE_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, PlacedFeatures.BLUE_GEODE_ORE.location());
        public static final ResourceKey<ConfiguredFeature<?,?>> RED_GEODE_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, PlacedFeatures.RED_GEODE_ORE.location());
        public static final ResourceKey<ConfiguredFeature<?,?>> PURPLE_GEODE_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, PlacedFeatures.PURPLE_GEODE_ORE.location());
    }
}
