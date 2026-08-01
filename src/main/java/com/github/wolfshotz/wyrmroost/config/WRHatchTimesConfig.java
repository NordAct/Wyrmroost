package com.github.wolfshotz.wyrmroost.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class WRHatchTimesConfig {
    public static final WRHatchTimesConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        Pair<WRHatchTimesConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(WRHatchTimesConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.ConfigValue<Integer> overworldDrake;
    public final ModConfigSpec.ConfigValue<Integer> silverGlider;
    public final ModConfigSpec.ConfigValue<Integer> roostStalker;
    public final ModConfigSpec.ConfigValue<Integer> butterflyLeviathan;
    public final ModConfigSpec.ConfigValue<Integer> dragonFruitDrake;
    public final ModConfigSpec.ConfigValue<Integer> canariWyvern;
    public final ModConfigSpec.ConfigValue<Integer> royalRed;
    public final ModConfigSpec.ConfigValue<Integer> alpine;

    WRHatchTimesConfig(ModConfigSpec.Builder builder) {
        builder.comment("All egg hatch times in ticks (1 second = 20 ticks)");

        overworldDrake = builder.define("overworld_drake", 18000);
        silverGlider = builder.define("silver_glider", 12000);
        roostStalker = builder.define("roost_stalker", 6000);
        butterflyLeviathan = builder.define("butterfly_leviathan", 40000);
        dragonFruitDrake = builder.define("dragon_fruit_drake", 9600);
        canariWyvern = builder.define("canari_wyvern", 6000);
        royalRed = builder.define("royal_red", 72000);
        alpine = builder.define("alpine", 12000);
    }
}
