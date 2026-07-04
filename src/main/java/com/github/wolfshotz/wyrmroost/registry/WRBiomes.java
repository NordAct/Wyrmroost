package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class WRBiomes {
    public static class Tags {
        private static TagKey<Biome> tag(String path) {
            return TagKey.create(Registries.BIOME, Wyrmroost.rl(path));
        }
    }
}
