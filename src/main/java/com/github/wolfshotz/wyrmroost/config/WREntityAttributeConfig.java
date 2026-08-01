package com.github.wolfshotz.wyrmroost.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class WREntityAttributeConfig {
    public static final WREntityAttributeConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        Pair<WREntityAttributeConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(WREntityAttributeConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.ConfigValue<Double> alpineHealth;
    public final ModConfigSpec.ConfigValue<Double> alpineGroundSpeed;
    public final ModConfigSpec.ConfigValue<Double> alpineKnockbackResistance;
    public final ModConfigSpec.ConfigValue<Double> alpineAttackDamage;
    public final ModConfigSpec.ConfigValue<Double> alpineFlyingSpeed;
    public final ModConfigSpec.ConfigValue<Double> alpineProjectileDamage;

    public final ModConfigSpec.ConfigValue<Double> butterflyLeviathanHealth;
    public final ModConfigSpec.ConfigValue<Double> butterflyLeviathanGroundSpeed;
    public final ModConfigSpec.ConfigValue<Double> butterflyLeviathanKnockbackResistance;
    public final ModConfigSpec.ConfigValue<Double> butterflyLeviathanAttackDamage;
    public final ModConfigSpec.ConfigValue<Double> butterflyLeviathanSwimSpeed;

    public final ModConfigSpec.ConfigValue<Double> canariWyvernHealth;
    public final ModConfigSpec.ConfigValue<Double> canariWyvernGroundSpeed;
    public final ModConfigSpec.ConfigValue<Double> canariWyvernAttackDamage;
    public final ModConfigSpec.ConfigValue<Double> canariWyvernFlyingSpeed;

    public final ModConfigSpec.ConfigValue<Double> dragonFruitDrakeHealth;
    public final ModConfigSpec.ConfigValue<Double> dragonFruitDrakeGroundSpeed;
    public final ModConfigSpec.ConfigValue<Double> dragonFruitDrakeAttackDamage;

    public final ModConfigSpec.ConfigValue<Double> overworldDrakeHealth;
    public final ModConfigSpec.ConfigValue<Double> overworldDrakeGroundSpeed;
    public final ModConfigSpec.ConfigValue<Double> overworldDrakeKnockbackResistance;
    public final ModConfigSpec.ConfigValue<Double> overworldDrakeAttackDamage;
    public final ModConfigSpec.ConfigValue<Double> overworldDrakeAttackKnockback;

    public final ModConfigSpec.ConfigValue<Double> roostStalkerHealth;
    public final ModConfigSpec.ConfigValue<Double> roostStalkerHealthTamed;
    public final ModConfigSpec.ConfigValue<Double> roostStalkerGroundSpeed;
    public final ModConfigSpec.ConfigValue<Double> roostStalkerAttackDamage;

    public final ModConfigSpec.ConfigValue<Double> royalRedHealthMale;
    public final ModConfigSpec.ConfigValue<Double> royalRedHealthFemale;
    public final ModConfigSpec.ConfigValue<Double> royalRedGroundSpeedMale;
    public final ModConfigSpec.ConfigValue<Double> royalRedGroundSpeedFemale;
    public final ModConfigSpec.ConfigValue<Double> royalRedKnockbackResistanceMale;
    public final ModConfigSpec.ConfigValue<Double> royalRedKnockbackResistanceFemale;
    public final ModConfigSpec.ConfigValue<Double> royalRedAttackDamageMale;
    public final ModConfigSpec.ConfigValue<Double> royalRedAttackDamageFemale;
    public final ModConfigSpec.ConfigValue<Double> royalRedAttackKnockbackMale;
    public final ModConfigSpec.ConfigValue<Double> royalRedAttackKnockbackFemale;
    public final ModConfigSpec.ConfigValue<Double> royalRedFlyingSpeedMale;
    public final ModConfigSpec.ConfigValue<Double> royalRedFlyingSpeedFemale;
    public final ModConfigSpec.ConfigValue<Double> royalRedProjectileDamageMale;
    public final ModConfigSpec.ConfigValue<Double> royalRedProjectileDamageFemale;

    public final ModConfigSpec.ConfigValue<Double> silverGliderHealth;
    public final ModConfigSpec.ConfigValue<Double> silverGliderGroundSpeed;
    public final ModConfigSpec.ConfigValue<Double> silverGliderFlyingSpeed;

    WREntityAttributeConfig(ModConfigSpec.Builder builder) {
        builder.push("Alpine Dragon");
        alpineHealth = builder.comment("Health").define("health", 40d);
        alpineGroundSpeed = builder.comment("Ground Speed").define("ground_speed", 0.22d);
        alpineKnockbackResistance = builder.comment("Knockback Resistance").define("knockback_resistance", 1d);
        alpineAttackDamage = builder.comment("Attack Damage").define("attack_damage", 3d);
        alpineFlyingSpeed = builder.comment("Flying Speed").define("flying_speed", 0.185d);
        alpineProjectileDamage = builder.comment("Projectile Damage").define("projectile_damage", 1d);
        builder.pop();

        builder.push("Butterfly Leviathan");
        butterflyLeviathanHealth = builder.comment("Health").define("health", 180d);
        butterflyLeviathanGroundSpeed = builder.comment("Ground Speed").define("ground_speed", 0.08d);
        butterflyLeviathanKnockbackResistance = builder.comment("Knockback Resistance").define("knockback_resistance", 1d);
        butterflyLeviathanAttackDamage = builder.comment("Attack Damage").define("attack_damage", 14d);
        butterflyLeviathanSwimSpeed = builder.comment("Swim Speed").define("swim_speed", 0.3d);
        builder.pop();

        builder.push("Canari Wyvern");
        canariWyvernHealth = builder.comment("Health").define("health", 12d);
        canariWyvernGroundSpeed = builder.comment("Ground Speed").define("ground_speed", 0.2d);
        canariWyvernAttackDamage = builder.comment("Attack Damage").define("attack_damage", 3d);
        canariWyvernFlyingSpeed = builder.comment("Flying Speed").define("flying_speed", 0.1d);
        builder.pop();

        builder.push("Dragon Fruit Drake");
        dragonFruitDrakeHealth = builder.comment("Health").define("health", 15d);
        dragonFruitDrakeGroundSpeed = builder.comment("Ground Speed").define("ground_speed", 0.23d);
        dragonFruitDrakeAttackDamage = builder.comment("Attack Damage").define("attack_damage", 3d);
        builder.pop();

        builder.push("Overworld Drake");
        overworldDrakeHealth = builder.comment("Health").define("health", 70d);
        overworldDrakeGroundSpeed = builder.comment("Ground Speed").define("ground_speed", 0.2125d);
        overworldDrakeKnockbackResistance = builder.comment("Knockback Resistance").define("knockback_resistance", 0.75d);
        overworldDrakeAttackDamage = builder.comment("Attack Damage").define("attack_damage", 8d);
        overworldDrakeAttackKnockback = builder.comment("Attack Knockback").define("attack_knockback", 2.85d);
        builder.pop();

        builder.push("Roost Stalker");
        roostStalkerHealth = builder.comment("Health").define("health", 8d);
        roostStalkerHealthTamed = builder.comment("Health when tamed").define("health_tamed", 20d);
        roostStalkerGroundSpeed = builder.comment("Ground Speed").define("ground_speed", 0.285d);
        roostStalkerAttackDamage = builder.comment("Attack Damage").define("attack_damage", 2d);
        builder.pop();

        builder.push("Royal Red Male");
        royalRedHealthMale = builder.comment("Health").define("health", 120d);
        royalRedGroundSpeedMale = builder.comment("Ground Speed").define("ground_speed", 0.2275d);
        royalRedKnockbackResistanceMale = builder.comment("Knockback Resistance").define("knockback_resistance", 1d);
        royalRedAttackDamageMale = builder.comment("Attack Damage").define("attack_damage", 12d);
        royalRedAttackKnockbackMale = builder.comment("Attack Knockback").define("attack_knockback", 3d);
        royalRedFlyingSpeedMale = builder.comment("Flying Speed").define("flying_speed", 0.125d);
        royalRedProjectileDamageMale = builder.comment("Projectile Damage").define("projectile_damage", 4d);
        builder.pop();

        builder.push("Royal Red Female");
        royalRedHealthFemale = builder.comment("Health").define("health", 130d);
        royalRedGroundSpeedFemale = builder.comment("Ground Speed").define("ground_speed", 0.22d);
        royalRedAttackDamageFemale = builder.comment("Attack Damage").define("attack_damage", 12d);
        royalRedAttackKnockbackFemale = builder.comment("Attack Knockback").define("attack_knockback", 4d);
        royalRedKnockbackResistanceFemale = builder.comment("Knockback Resistance").define("knockback_resistance", 1d);
        royalRedFlyingSpeedFemale = builder.comment("Flying Speed").define("flying_speed", 0.121d);
        royalRedProjectileDamageFemale = builder.comment("Projectile Damage").define("projectile_damage", 4d);
        builder.pop();

        builder.push("Silver Glider");
        silverGliderHealth = builder.comment("Health").define("health", 20d);
        silverGliderGroundSpeed = builder.comment("Ground Speed").define("ground_speed", 0.23d);
        silverGliderFlyingSpeed = builder.comment("Flying Speed").define("flying_speed", 0.12d);
        builder.pop();
    }
}
