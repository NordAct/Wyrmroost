package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class WRDamageTypes {
    public static final ResourceKey<DamageType> WIND_GUST = ResourceKey.create(Registries.DAMAGE_TYPE, Wyrmroost.rl("wind_gust"));
    public static final ResourceKey<DamageType> FIRE_BREATH_0 = ResourceKey.create(Registries.DAMAGE_TYPE, Wyrmroost.rl("fire_breath_0"));
    public static final ResourceKey<DamageType> FIRE_BREATH_1 = ResourceKey.create(Registries.DAMAGE_TYPE, Wyrmroost.rl("fire_breath_1"));
}
