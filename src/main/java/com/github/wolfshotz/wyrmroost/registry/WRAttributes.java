package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WRAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(Registries.ATTRIBUTE, Wyrmroost.MOD_ID);

    public static final Holder<Attribute> PROJECTILE_DAMAGE = ranged("generic.projectile_damage", 2d, 0, 2048d);

    private static Holder<Attribute> ranged(String name, double defaultValue, double min, double max) {
        return register(name.toLowerCase().replace('.', '_'), () -> new RangedAttribute("attribute.name." + name, defaultValue, min, max));
    }

    private static Holder<Attribute> register(String name, Supplier<Attribute> attribute) {
        return REGISTRY.register(name, attribute);
    }
}
