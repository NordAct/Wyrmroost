package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.items.*;
import com.github.wolfshotz.wyrmroost.items.arrow.BlueGeodeTippedArrowItem;
import com.github.wolfshotz.wyrmroost.items.arrow.PurpleGeodeTippedArrowItem;
import com.github.wolfshotz.wyrmroost.items.arrow.RedGeodeTippedArrowItem;
import com.github.wolfshotz.wyrmroost.items.base.ArmorBase;
import com.github.wolfshotz.wyrmroost.items.base.ToolMaterials;
import com.github.wolfshotz.wyrmroost.items.staff.DragonStaffItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WRItems
{
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, Wyrmroost.MOD_ID);

    public static final Holder<Item> LDWYRM = register("desert_wyrm", LDWyrmItem::new);
    public static final Holder<Item> DRAGON_EGG = register("dragon_egg", DragonEggItem::new);
    public static final Holder<Item> SOUL_CRYSTAL = register("soul_crystal", SoulCrystalItem::new);
    public static final Holder<Item> DRAGON_STAFF = register("dragon_staff", DragonStaffItem::new);
    public static final Holder<Item> COIN_DRAGON = register("coin_dragon", CoinDragonItem::new);
    public static final Holder<Item> TRUMPET = register("trumpet", TrumpetItem::new);
//    public static final Supplier<Item> SILK_GLAND = register("orbwyrm_silk_gland", SilkGlandItem::new);
//    public static final Supplier<Item> FOG_WRAITH_TAILS = register("fog_wraith_tails", FogWraithTailsItem::new);

    public static final Holder<Item> BLUE_GEODE = register("blue_geode");
    public static final Holder<Item> RED_GEODE = register("red_geode");
    public static final Holder<Item> PURPLE_GEODE = register("purple_geode");
    public static final Holder<Item> PLATINUM_INGOT = register("platinum_ingot");
    public static final Holder<Item> DRAKE_BACKPLATE = register("drake_backplate");

    public static final Holder<Item> BLUE_GEODE_SWORD = register("blue_geode_sword", () -> new SwordItem(ToolMaterials.BLUE_GEODE, builder().attributes(SwordItem.createAttributes(ToolMaterials.BLUE_GEODE, 5, -2.4f))));
    public static final Holder<Item> BLUE_GEODE_PICKAXE = register("blue_geode_pickaxe", () -> new PickaxeItem(ToolMaterials.BLUE_GEODE, builder().attributes(PickaxeItem.createAttributes(ToolMaterials.BLUE_GEODE, 3, -2.8f))));
    public static final Holder<Item> BLUE_GEODE_AXE = register("blue_geode_axe", () -> new AxeItem(ToolMaterials.BLUE_GEODE, builder().attributes(AxeItem.createAttributes(ToolMaterials.BLUE_GEODE, 7.5f, -3f))));
    public static final Holder<Item> BLUE_GEODE_SHOVEL = register("blue_geode_shovel", () -> new ShovelItem(ToolMaterials.BLUE_GEODE, builder().attributes(ShovelItem.createAttributes(ToolMaterials.BLUE_GEODE, 3.5f, -3f))));
    public static final Holder<Item> BLUE_GEODE_HOE = register("blue_geode_hoe", () -> new HoeItem(ToolMaterials.BLUE_GEODE, builder().attributes(HoeItem.createAttributes(ToolMaterials.BLUE_GEODE, 1, 2.5f))));
    public static final Holder<Item> BLUE_GEODE_HELMET = register("blue_geode_helmet", () -> new ArmorBase(WRArmorMaterials.BLUE_GEODE, ArmorItem.Type.HELMET, builder()));
    public static final Holder<Item> BLUE_GEODE_CHESTPLATE = register("blue_geode_chestplate", () -> new ArmorBase(WRArmorMaterials.BLUE_GEODE, ArmorItem.Type.BODY, builder()));
    public static final Holder<Item> BLUE_GEODE_LEGGINGS = register("blue_geode_leggings", () -> new ArmorBase(WRArmorMaterials.BLUE_GEODE, ArmorItem.Type.LEGGINGS, builder()));
    public static final Holder<Item> BLUE_GEODE_BOOTS = register("blue_geode_boots", () -> new ArmorBase(WRArmorMaterials.BLUE_GEODE, ArmorItem.Type.BOOTS, builder()));
    public static final Holder<Item> BLUE_GEODE_ARROW = register("blue_geode_tipped_arrow", () -> new BlueGeodeTippedArrowItem(3));

    public static final Holder<Item> RED_GEODE_SWORD = register("red_geode_sword", () -> new SwordItem(ToolMaterials.RED_GEODE, builder().attributes(SwordItem.createAttributes(ToolMaterials.RED_GEODE, 6, -2.4f))));
    public static final Holder<Item> RED_GEODE_PICKAXE = register("red_geode_pickaxe", () -> new PickaxeItem(ToolMaterials.RED_GEODE, builder().attributes(PickaxeItem.createAttributes(ToolMaterials.RED_GEODE, 4, -2.8f))));
    public static final Holder<Item> RED_GEODE_AXE = register("red_geode_axe", () -> new AxeItem(ToolMaterials.RED_GEODE, builder().attributes(AxeItem.createAttributes(ToolMaterials.RED_GEODE, 8f, -3))));
    public static final Holder<Item> RED_GEODE_SHOVEL = register("red_geode_shovel", () -> new ShovelItem(ToolMaterials.RED_GEODE, builder().attributes(ShovelItem.createAttributes(ToolMaterials.RED_GEODE, 4f, -3f))));
    public static final Holder<Item> RED_GEODE_HOE = register("red_geode_hoe", () -> new HoeItem(ToolMaterials.RED_GEODE, builder().attributes(HoeItem.createAttributes(ToolMaterials.RED_GEODE, 2, 0))));
    public static final Holder<Item> RED_GEODE_HELMET = register("red_geode_helmet", () -> new ArmorBase(WRArmorMaterials.RED_GEODE, ArmorItem.Type.HELMET, builder()));
    public static final Holder<Item> RED_GEODE_CHESTPLATE = register("red_geode_chestplate", () -> new ArmorBase(WRArmorMaterials.RED_GEODE, ArmorItem.Type.BODY, builder()));
    public static final Holder<Item> RED_GEODE_LEGGINGS = register("red_geode_leggings", () -> new ArmorBase(WRArmorMaterials.RED_GEODE, ArmorItem.Type.LEGGINGS, builder()));
    public static final Holder<Item> RED_GEODE_BOOTS = register("red_geode_boots", () -> new ArmorBase(WRArmorMaterials.RED_GEODE, ArmorItem.Type.BOOTS, builder()));
    public static final Holder<Item> RED_GEODE_ARROW = register("red_geode_tipped_arrow", () -> new RedGeodeTippedArrowItem(3.5));

    public static final Holder<Item> PURPLE_GEODE_SWORD = register("purple_geode_sword", () -> new SwordItem(ToolMaterials.PURPLE_GEODE, builder().attributes(SwordItem.createAttributes(ToolMaterials.PURPLE_GEODE, 8, -2.4f)).rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_PICKAXE = register("purple_geode_pickaxe", () -> new PickaxeItem(ToolMaterials.PURPLE_GEODE, builder().attributes(PickaxeItem.createAttributes(ToolMaterials.PURPLE_GEODE, 6, -3f)).rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_AXE = register("purple_geode_axe", () -> new AxeItem(ToolMaterials.PURPLE_GEODE, builder().attributes(AxeItem.createAttributes(ToolMaterials.PURPLE_GEODE, 10f, -2.9f)).rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_SHOVEL = register("purple_geode_shovel", () -> new ShovelItem(ToolMaterials.PURPLE_GEODE, builder().attributes(ShovelItem.createAttributes(ToolMaterials.PURPLE_GEODE, 6.5f, -2.7f)).rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_HOE = register("purple_geode_hoe", () -> new HoeItem(ToolMaterials.PURPLE_GEODE, builder().attributes(HoeItem.createAttributes(ToolMaterials.PURPLE_GEODE, 0, 4f)).rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_HELMET = register("purple_geode_helmet", () -> new ArmorBase(WRArmorMaterials.PURPLE_GEODE, ArmorItem.Type.HELMET, builder().rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_CHESTPLATE = register("purple_geode_chestplate", () -> new ArmorBase(WRArmorMaterials.PURPLE_GEODE, ArmorItem.Type.BODY, builder().rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_LEGGINGS = register("purple_geode_leggings", () -> new ArmorBase(WRArmorMaterials.PURPLE_GEODE, ArmorItem.Type.LEGGINGS, builder().rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_BOOTS = register("purple_geode_boots", () -> new ArmorBase(WRArmorMaterials.PURPLE_GEODE, ArmorItem.Type.BOOTS, builder().rarity(Rarity.RARE)));
    public static final Holder<Item> PURPLE_GEODE_ARROW = register("purple_geode_tipped_arrow", () -> new PurpleGeodeTippedArrowItem(4));

    public static final Holder<Item> PLATINUM_SWORD = register("platinum_sword", () -> new SwordItem(ToolMaterials.PLATINUM, builder().attributes(SwordItem.createAttributes(ToolMaterials.PLATINUM, 5, -2.4f))));
    public static final Holder<Item> PLATINUM_PICKAXE = register("platinum_pickaxe", () -> new PickaxeItem(ToolMaterials.PLATINUM, builder().attributes(PickaxeItem.createAttributes(ToolMaterials.PLATINUM, 3, -2.8f))));
    public static final Holder<Item> PLATINUM_AXE = register("platinum_axe", () -> new AxeItem(ToolMaterials.PLATINUM, builder().attributes(AxeItem.createAttributes(ToolMaterials.PLATINUM, 7.5f, -2.8f))));
    public static final Holder<Item> PLATINUM_SHOVEL = register("platinum_shovel", () -> new ShovelItem(ToolMaterials.PLATINUM, builder().attributes(ShovelItem.createAttributes(ToolMaterials.PLATINUM, 3f, -3f))));
    public static final Holder<Item> PLATINUM_HOE = register("platinum_hoe", () -> new HoeItem(ToolMaterials.PLATINUM, builder().attributes(HoeItem.createAttributes(ToolMaterials.PLATINUM, 0, -1))));
    public static final Holder<Item> PLATINUM_HELMET = register("platinum_helmet", () -> new ArmorBase(WRArmorMaterials.PLATINUM, ArmorItem.Type.HELMET, builder()));
    public static final Holder<Item> PLATINUM_CHESTPLATE = register("platinum_chestplate", () -> new ArmorBase(WRArmorMaterials.PLATINUM, ArmorItem.Type.BODY, builder()));
    public static final Holder<Item> PLATINUM_LEGGINGS = register("platinum_leggings", () -> new ArmorBase(WRArmorMaterials.PLATINUM, ArmorItem.Type.LEGGINGS, builder()));
    public static final Holder<Item> PLATINUM_BOOTS = register("platinum_boots", () -> new ArmorBase(WRArmorMaterials.PLATINUM, ArmorItem.Type.BOOTS, builder()));

    public static final Holder<Item> DRAKE_HELMET = register("drake_helmet", () -> new DrakeArmorItem(ArmorItem.Type.HELMET, builder().rarity(Rarity.RARE)));
    public static final Holder<Item> DRAKE_CHESTPLATE = register("drake_chestplate", () -> new DrakeArmorItem(ArmorItem.Type.BODY, builder().rarity(Rarity.RARE)));
    public static final Holder<Item> DRAKE_LEGGINGS = register("drake_leggings", () -> new DrakeArmorItem(ArmorItem.Type.LEGGINGS, builder().rarity(Rarity.RARE)));
    public static final Holder<Item> DRAKE_BOOTS = register("drake_boots", () -> new DrakeArmorItem(ArmorItem.Type.BOOTS, builder().rarity(Rarity.RARE)));

    public static final Holder<Item> RAW_LOWTIER_MEAT = register("raw_lowtier_meat", () -> new Item(builder().food(Foods.RAW_LOWTIER_MEAT)));
    public static final Holder<Item> RAW_COMMON_MEAT = register("raw_common_meat", () -> new Item(builder().food(Foods.RAW_COMMON_MEAT)));
    public static final Holder<Item> RAW_APEX_MEAT = register("raw_apex_meat", () -> new Item(builder().food(Foods.RAW_APEX_MEAT)));
    public static final Holder<Item> RAW_BEHEMOTH_MEAT = register("raw_behemoth_meat", () -> new Item(builder().food(Foods.RAW_BEHEMOTH_MEAT)));
    public static final Holder<Item> COOKED_LOWTIER_MEAT = register("cooked_lowtier_meat", () -> new Item(builder().food(Foods.COOKED_LOWTIER_MEAT)));
    public static final Holder<Item> COOKED_COMMON_MEAT = register("cooked_common_meat", () -> new Item(builder().food(Foods.COOKED_COMMON_MEAT)));
    public static final Holder<Item> COOKED_APEX_MEAT = register("cooked_apex_meat", () -> new Item(builder().food(Foods.COOKED_APEX_MEAT)));
    public static final Holder<Item> COOKED_BEHEMOTH_MEAT = register("cooked_behemoth_meat", () -> new Item(builder().food(Foods.COOKED_BEHEMOTH_MEAT)));
    public static final Holder<Item> COOKED_MINUTUS = register("cooked_desertwyrm", () -> new Item(builder().food(Foods.COOKED_DESERTWYRM)));
    public static final Holder<Item> JEWELLED_APPLE = register("jewelled_apple", () -> new Item(builder().food(Foods.JEWELLED_APPLE)));

    public static final Holder<Item> DRAGON_ARMOR_IRON = register("iron_dragon_armor", () -> new DragonArmorItem(ArmorMaterials.IRON, builder()));
    public static final Holder<Item> DRAGON_ARMOR_GOLD = register("gold_dragon_armor", () -> new DragonArmorItem(ArmorMaterials.GOLD, builder()));
    public static final Holder<Item> DRAGON_ARMOR_DIAMOND = register("diamond_dragon_armor", () -> new DragonArmorItem(ArmorMaterials.DIAMOND, builder()));
    public static final Holder<Item> DRAGON_ARMOR_PLATINUM = register("platinum_dragon_armor", () -> new DragonArmorItem( WRArmorMaterials.PLATINUM, builder()));
    public static final Holder<Item> DRAGON_ARMOR_BLUE_GEODE = register("blue_geode_dragon_armor", () -> new DragonArmorItem(WRArmorMaterials.BLUE_GEODE, builder()));
    public static final Holder<Item> DRAGON_ARMOR_RED_GEODE = register("red_geode_dragon_armor", () -> new DragonArmorItem(WRArmorMaterials.RED_GEODE, builder()));
    public static final Holder<Item> DRAGON_ARMOR_PURPLE_GEODE = register("purple_geode_dragon_armor", () -> new DragonArmorItem(WRArmorMaterials.PURPLE_GEODE, builder().rarity(Rarity.RARE)));

    static Holder<Item> register(String name, Supplier<Item> item) {
        return REGISTRY.register(name, item);
    }

    static Holder<Item> register(String name)
    {
        return REGISTRY.register(name, () -> new Item(builder()));
    }

    public static Item.Properties builder() {
        return new Item.Properties();
    }

    private static class Foods
    {
        static final FoodProperties RAW_LOWTIER_MEAT = new FoodProperties.Builder().nutrition(1).saturationModifier(0.1f).build();
        static final FoodProperties RAW_COMMON_MEAT = new FoodProperties.Builder().nutrition(3).saturationModifier(0.3f).build();
        static final FoodProperties RAW_APEX_MEAT = new FoodProperties.Builder().nutrition(5).saturationModifier(0.45f).build();
        static final FoodProperties RAW_BEHEMOTH_MEAT = new FoodProperties.Builder().nutrition(7).saturationModifier(0.7f).build();
        static final FoodProperties COOKED_LOWTIER_MEAT = new FoodProperties.Builder().nutrition(3).saturationModifier(0.7f).build();
        static final FoodProperties COOKED_COMMON_MEAT = new FoodProperties.Builder().nutrition(8).saturationModifier(0.8f).build();
        static final FoodProperties COOKED_APEX_MEAT = new FoodProperties.Builder().nutrition(16).saturationModifier(1f).build();
        static final FoodProperties COOKED_BEHEMOTH_MEAT = new FoodProperties.Builder().nutrition(20).saturationModifier(2f).build();
        static final FoodProperties COOKED_DESERTWYRM = new FoodProperties.Builder().nutrition(10).saturationModifier(1f).build();
        static final FoodProperties JEWELLED_APPLE = new FoodProperties.Builder().nutrition(8).saturationModifier(0.9f).alwaysEdible()
                .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 800), 1f)
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 2), 1f)
                .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 800), 1f)
                .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 6000, 2), 1f)
                .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 800), 1f)
                .build();
    }

    public static class Tags
    {
        public static final TagKey<Item> GEODES = tag("geodes");
        public static final TagKey<Item> DRAGON_MEATS = tag("dragon_meats");
        public static final TagKey<Item> PLATINUM_INGOTS = tag("platinum_ingots");
        public static final TagKey<Item> ALPINE_FOOD = tag("alpine_food");
        public static final TagKey<Item> BUTTERFLY_LEVIATHAN_FOOD = tag("butterfly_leviathan_food");
        public static final TagKey<Item> CANARI_WYVERN_FOOD = tag("canari_wyvern_food");
        public static final TagKey<Item> DRAGON_FRUIT_DRAKE_FOOD = tag("dragon_fruit_drake_food");
        public static final TagKey<Item> OVERWORLD_DRAKE_FOOD = tag("overworld_drake_food");
        public static final TagKey<Item> ROOST_STALKER_FOOD = tag("roost_stalker_food");
        public static final TagKey<Item> ROYAL_RED_FOOD = tag("royal_red_food");
        public static final TagKey<Item> SILVER_GLIDER_FOOD = tag("silver_glider_food");

        public static final TagKey<Item> ALPINE_BREEDING_ITEMS = tag("alpine_breeding_items");
        public static final TagKey<Item> BUTTERFLY_LEVIATHAN_BREEDING_ITEMS = tag("butterfly_leviathan_breeding_items");
        public static final TagKey<Item> CANARI_WYVERN_BREEDING_ITEMS = tag("canari_wyvern_breeding_items");
        public static final TagKey<Item> DRAGON_FRUIT_DRAKE_BREEDING_ITEMS = tag("dragon_fruit_drake_breeding_items");
        public static final TagKey<Item> OVERWORLD_DRAKE_BREEDING_ITEMS = tag("overworld_drake_breeding_items");
        public static final TagKey<Item> ROOST_STALKER_BREEDING_ITEMS = tag("roost_stalker_breeding_items");
        public static final TagKey<Item> ROYAL_RED_BREEDING_ITEMS = tag("royal_red_breeding_items");
        public static final TagKey<Item> SILVER_GLIDER_BREEDING_ITEMS = tag("silver_glider_breeding_items");

        public static final TagKey<Item> ACTIVATES_DRAGON_FRUIT_DRAKE_CROPS_GROWTH = tag("activates_dragon_fruit_drake_crops_growth");
        public static final TagKey<Item> ROOST_STALKER_TAMING_ITEMS = tag("roost_stalker_taming_items");

        public static final TagKey<Item> GEODE_TIPPED_ARROWS = tag("geode_tipped_arrows");

        private static TagKey<Item> tag(String path) {
            return ItemTags.create(Wyrmroost.rl(path));
        }
    }
}