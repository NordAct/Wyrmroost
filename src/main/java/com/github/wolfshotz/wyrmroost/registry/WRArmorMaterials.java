package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * @see net.minecraft.world.item.ArmorMaterial
 */
public class WRArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> REGISTRY = DeferredRegister.create(Registries.ARMOR_MATERIAL, Wyrmroost.MOD_ID);

    public static final Holder<ArmorMaterial> BLUE_GEODE = register("blue_geode", () -> new ArmorMaterial(
            Util.make(new HashMap<>(), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 7);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.BODY, 10);
            }),
            25,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of(WRItems.BLUE_GEODE.value()),
            List.of(
                    new ArmorMaterial.Layer(Wyrmroost.rl("blue_geode"), "", false),
                    new ArmorMaterial.Layer(Wyrmroost.rl("blue_geode"), "", false)
            ),
            1, 0
    ));
    public static final Holder<ArmorMaterial> RED_GEODE = register("red_geode", () -> new ArmorMaterial(
            Util.make(new HashMap<>(), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.BODY, 13);
            }),
            25,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of(WRItems.RED_GEODE.value()),
            List.of(
                    new ArmorMaterial.Layer(Wyrmroost.rl("red_geode"), "", false),
                    new ArmorMaterial.Layer(Wyrmroost.rl("red_geode"), "", false)
            ),
            2.5f, 0
    ));
    public static final Holder<ArmorMaterial> PURPLE_GEODE = register("purple_geode", () -> new ArmorMaterial(
                    Util.make(new HashMap<>(), map -> {
                        map.put(ArmorItem.Type.HELMET, 4);
                        map.put(ArmorItem.Type.CHESTPLATE, 10);
                        map.put(ArmorItem.Type.LEGGINGS, 7);
                        map.put(ArmorItem.Type.BOOTS, 4);
                        map.put(ArmorItem.Type.BODY, 18);
                    }),
                    28,
                    SoundEvents.ARMOR_EQUIP_DIAMOND,
                    () -> Ingredient.of(WRItems.PURPLE_GEODE.value()),
                    List.of(
                            new ArmorMaterial.Layer(Wyrmroost.rl("purple_geode"), "", false),
                            new ArmorMaterial.Layer(Wyrmroost.rl("purple_geode"), "", false)
                    ),
                    4, 0
            ));
    public static final Holder<ArmorMaterial> PLATINUM = register("platinum", () -> new ArmorMaterial(
                    Util.make(new HashMap<>(), map -> {
                        map.put(ArmorItem.Type.HELMET, 2);
                        map.put(ArmorItem.Type.CHESTPLATE, 7);
                        map.put(ArmorItem.Type.LEGGINGS, 5);
                        map.put(ArmorItem.Type.BOOTS, 2);
                        map.put(ArmorItem.Type.BODY, 7);
                    }),
                    10,
                    SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(WRItems.PLATINUM_INGOT.value()),
                    List.of(
                            new ArmorMaterial.Layer(Wyrmroost.rl("platinum"), "", false),
                            new ArmorMaterial.Layer(Wyrmroost.rl("platinum"), "", false)
                    ),
                    0.1f, 0
            ));
    public static final Holder<ArmorMaterial> DRAKE = register("drake", () -> new ArmorMaterial(
            Util.make(new HashMap<>(), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.BODY, 6);
            }),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.of(WRItems.DRAKE_BACKPLATE.value()),
            List.of(
                    new ArmorMaterial.Layer(Wyrmroost.rl("drake"), "", false),
                    new ArmorMaterial.Layer(Wyrmroost.rl("drake"), "", false)
            ),
            1.2f, 0
    ));

    private static Holder<ArmorMaterial> register(String name, Supplier<ArmorMaterial> supplier) {
        return REGISTRY.register(name, supplier);
    }
}
