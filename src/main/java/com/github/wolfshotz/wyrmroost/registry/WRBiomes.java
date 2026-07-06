package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class WRBiomes {
    public static class Tags {
        public static final TagKey<Biome> ALPINE_CAN_SPAWN = tag("alpine_can_spawn");
        public static final TagKey<Biome> BUTTERFLY_LEVIATHAN_CAN_SPAWN = tag("butterfly_leviathan_can_spawn");
        public static final TagKey<Biome> CANARI_WYVERN_CAN_SPAWN = tag("canari_wyvern_can_spawn");
        public static final TagKey<Biome> DRAGON_FRUIT_DRAKE_CAN_SPAWN = tag("dragon_fruit_drake_can_spawn");
        public static final TagKey<Biome> LESSER_DESERT_WYRM_CAN_SPAWN = tag("lesser_desert_wyrm_can_spawn");
        public static final TagKey<Biome> OVERWORLD_DRAKE_CAN_SPAWN = tag("overworld_drake_can_spawn");
        public static final TagKey<Biome> ROOST_STALKER_CAN_SPAWN = tag("roost_stalker_can_spawn");
        public static final TagKey<Biome> ROYAL_RED_CAN_SPAWN = tag("royal_red_can_spawn");
        public static final TagKey<Biome> SILVER_GLIDER_CAN_SPAWN = tag("silver_glider_can_spawn");

        private static TagKey<Biome> tag(String path) {
            return TagKey.create(Registries.BIOME, Wyrmroost.rl(path));
        }
    }
}
