package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

class RecipeData extends RecipeProvider {
    RecipeData(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    private ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count)
    {
        return ShapedRecipeBuilder.shaped(category, result, count);
    }

    private ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result) {
        return shaped(category, result, 1);
    }

    private ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int count) {
        return ShapelessRecipeBuilder.shapeless(category, result, count);
    }

    private ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result) {
        return shapeless(category, result, 1);
    }

    /**
     * @param ingredients first element is used for criterion, design accordingly.
     */
    private void shapeless(RecipeCategory category, RecipeOutput output, ItemLike result, @Nonnull ShapelessPair... ingredients) {
        final ShapelessRecipeBuilder builder = shapeless(category, result);
        for (ShapelessPair ingredient : ingredients) builder.requires(ingredient.item, ingredient.count);
        ItemLike firstIngredient = ingredients[0].item;
        builder.unlockedBy("has_" + firstIngredient.asItem().builtInRegistryHolder().getKey().location().getPath(), has(firstIngredient)).save(output);
    }

    private void armorSet(RecipeOutput output, ItemLike material, ItemLike helmet, ItemLike chest, ItemLike legs, ItemLike boots) {
        shaped(RecipeCategory.COMBAT, helmet).define('X', material).pattern("XXX").pattern("X X").unlockedBy("has_material", has(material)).save(output);
        shaped(RecipeCategory.COMBAT, chest).define('X', material).pattern("X X").pattern("XXX").unlockedBy("has_material", has(material)).pattern("XXX").save(output);
        shaped(RecipeCategory.COMBAT, legs).define('X', material).pattern("XXX").pattern("X X").unlockedBy("has_material", has(material)).pattern("X X").save(output);
        shaped(RecipeCategory.COMBAT, boots).define('X', material).pattern("X X").pattern("X X").unlockedBy("has_material", has(material)).save(output);
    }

    private void armorSet(RecipeOutput output, TagKey<Item> materials, ItemLike helmet, ItemLike chest, ItemLike legs, ItemLike boots) {
        shaped(RecipeCategory.COMBAT, helmet).define('X', materials).pattern("XXX").pattern("X X").unlockedBy("has_material", has(materials)).save(output);
        shaped(RecipeCategory.COMBAT, chest).define('X', materials).pattern("X X").pattern("XXX").unlockedBy("has_material", has(materials)).pattern("XXX").save(output);
        shaped(RecipeCategory.COMBAT, legs).define('X', materials).pattern("XXX").pattern("X X").unlockedBy("has_material", has(materials)).pattern("X X").save(output);
        shaped(RecipeCategory.COMBAT, boots).define('X', materials).pattern("X X").pattern("X X").unlockedBy("has_material", has(materials)).save(output);
    }

    private void toolSet(RecipeOutput output, ItemLike material, ItemLike sword, ItemLike pick, ItemLike axe, ItemLike shovel, ItemLike hoe) {
        shaped(RecipeCategory.COMBAT, sword).define('X', material).define('|', Items.STICK).pattern("X").pattern("X").pattern("|").unlockedBy("has_material", has(material)).save(output);
        shaped(RecipeCategory.TOOLS, pick).define('X', material).define('|', Items.STICK).pattern("XXX").pattern(" | ").pattern(" | ").unlockedBy("has_material", has(material)).save(output);
        shaped(RecipeCategory.TOOLS, axe).define('X', material).define('|', Items.STICK).pattern("XX").pattern("X|").pattern(" |").unlockedBy("has_material", has(material)).save(output);
        shaped(RecipeCategory.TOOLS, shovel).define('X', material).define('|', Items.STICK).pattern("X").pattern("|").pattern("|").unlockedBy("has_material", has(material)).save(output);
        shaped(RecipeCategory.TOOLS, hoe).define('X', material).define('|', Items.STICK).pattern("XX").pattern(" |").pattern(" |").unlockedBy("has_material", has(material)).save(output);
    }

    private void toolSet(RecipeOutput output, TagKey<Item> materials, ItemLike sword, ItemLike pick, ItemLike axe, ItemLike shovel, ItemLike hoe) {
        shaped(RecipeCategory.COMBAT,sword).define('X', materials).define('|', Items.STICK).pattern("X").pattern("X").pattern("|").unlockedBy("has_material", has(materials)).save(output);
        shaped(RecipeCategory.TOOLS, pick).define('X', materials).define('|', Items.STICK).pattern("XXX").pattern(" | ").pattern(" | ").unlockedBy("has_material", has(materials)).save(output);
        shaped(RecipeCategory.TOOLS, axe).define('X', materials).define('|', Items.STICK).pattern("XX").pattern("X|").pattern(" |").unlockedBy("has_material", has(materials)).save(output);
        shaped(RecipeCategory.TOOLS, shovel).define('X', materials).define('|', Items.STICK).pattern("X").pattern("|").pattern("|").unlockedBy("has_material", has(materials)).save(output);
        shaped(RecipeCategory.TOOLS, hoe).define('X', materials).define('|', Items.STICK).pattern("XX").pattern(" |").pattern(" |").unlockedBy("has_material", has(materials)).save(output);
    }

    private void storageBlock(RecipeOutput output, ItemLike material, ItemLike block) {
        shaped(RecipeCategory.MISC, block).define('X', material).pattern("XXX").pattern("XXX").pattern("XXX").unlockedBy("has_" + material.asItem().builtInRegistryHolder().getKey().location().getPath(), has(material)).save(output);
        shapeless(RecipeCategory.MISC, material, 9).requires(block).unlockedBy("has_" + block.asItem().builtInRegistryHolder().getKey().location().getPath(), has(block)).save(output);
    }

    private void smelt(RecipeOutput output, ItemLike ingredient, ItemLike result, float experience, int time, boolean food) {
        String id = result.asItem().builtInRegistryHolder().getKey().location().getPath();
        String idIngredient = ingredient.asItem().builtInRegistryHolder().getKey().location().getPath();
        String criterion = "has_" + ingredient.asItem().builtInRegistryHolder().getKey().location().getPath();

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), food ? RecipeCategory.FOOD : RecipeCategory.MISC, result, experience, time).unlockedBy(criterion, has(ingredient)).save(output, Wyrmroost.rl((id + "_from_smelting_" + idIngredient)));
        if (food)
        {
            SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(ingredient), RecipeCategory.FOOD, result, experience, time + 500).unlockedBy(criterion, has(ingredient)).save(output, Wyrmroost.rl(id + "_from_campfire_" + idIngredient));
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(ingredient), RecipeCategory.FOOD, result, experience, time - 100).unlockedBy(criterion, has(ingredient)).save(output, Wyrmroost.rl(id + "_from_smoking_" + idIngredient));
        }
        else
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredient), RecipeCategory.MISC, result, experience, time - 100).unlockedBy(criterion, has(ingredient)).save(output, Wyrmroost.rl(id + "_from_blasting_" + idIngredient));
    }

    private void smelt(RecipeOutput output, ItemLike ingredient, ItemLike result, float experience, int time) { smelt(output, ingredient, result, experience, time, false); }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // Misc stuff
        shaped(RecipeCategory.MISC, WRItems.SOUL_CRYSTAL.value()).define('X', WRItems.BLUE_GEODE.value()).define('#', Items.ENDER_EYE).pattern(" X ").pattern("X#X").pattern(" X ").unlockedBy("has_eye", has(Items.ENDER_EYE)).save(output);
        shaped(RecipeCategory.MISC, WRItems.DRAGON_STAFF.value()).define('X', WRItems.RED_GEODE.value()).define('|', Items.BLAZE_ROD).pattern("X").pattern("|").unlockedBy("has_geode", has(WRItems.RED_GEODE.value())).save(output);

        shaped(RecipeCategory.COMBAT, WRItems.BLUE_GEODE_ARROW.value(), 8).define('G', WRItems.BLUE_GEODE.value()).define('|', Items.STICK).define('F', Items.FEATHER).pattern("G").pattern("|").pattern("F").unlockedBy("has_geode", has(WRItems.BLUE_GEODE.value())).save(output);
        shaped(RecipeCategory.COMBAT, WRItems.RED_GEODE_ARROW.value(), 8).define('G', WRItems.RED_GEODE.value()).define('|', Items.STICK).define('F', Items.FEATHER).pattern("G").pattern("|").pattern("F").unlockedBy("has_geode", has(WRItems.RED_GEODE.value())).save(output);
        shaped(RecipeCategory.COMBAT, WRItems.PURPLE_GEODE_ARROW.value(), 8).define('G', WRItems.PURPLE_GEODE.value()).define('|', Items.STICK).define('F', Items.FEATHER).pattern("G").pattern("|").pattern("F").unlockedBy("has_geode", has(WRItems.PURPLE_GEODE.value())).save(output);

        stonecutterResultFromBase(output, RecipeCategory.DECORATIONS, WRBlocks.CHISELED_PLATINUM_BLOCK.value(), WRBlocks.PLATINUM_BLOCK.value(), 2);
        // Materials
        storageBlock(output, WRItems.BLUE_GEODE.value(), WRBlocks.BLUE_GEODE_BLOCK.value());
        smelt(output, WRBlocks.BLUE_GEODE_ORE.value(), WRItems.BLUE_GEODE.value(), 1f, 200);
        storageBlock(output, WRItems.RED_GEODE.value(), WRBlocks.RED_GEODE_BLOCK.value());
        smelt(output, WRBlocks.RED_GEODE_ORE.value(), WRItems.RED_GEODE.value(), 1.5f, 200);
        storageBlock(output, WRItems.PURPLE_GEODE.value(), WRBlocks.PURPLE_GEODE_BLOCK.value());
        smelt(output, WRBlocks.PURPLE_GEODE_ORE.value(), WRItems.PURPLE_GEODE.value(), 2f, 200);

        storageBlock(output, WRItems.RAW_PLATINUM.value(), WRBlocks.RAW_PLATINUM_BLOCK.value());
        shaped(RecipeCategory.MISC, WRBlocks.PLATINUM_BLOCK.value()).define('X', WRItems.Tags.PLATINUM_INGOTS).pattern("XXX").pattern("XXX").pattern("XXX").unlockedBy("has_platinum", has(WRItems.PLATINUM_INGOT.value())).save(output);
        shapeless(RecipeCategory.MISC, WRItems.PLATINUM_INGOT.value(), 9).requires(WRBlocks.PLATINUM_BLOCK.value()).unlockedBy("has_platinum", has(WRBlocks.PLATINUM_BLOCK.value())).save(output);
        smelt(output, WRItems.RAW_PLATINUM.value(), WRItems.PLATINUM_INGOT.value(), 0.7f, 200, false);
        smelt(output, WRBlocks.PLATINUM_ORE.value(), WRItems.PLATINUM_INGOT.value(), 0.7f, 200);

        // Tools
        toolSet(output, WRItems.BLUE_GEODE.value(), WRItems.BLUE_GEODE_SWORD.value(), WRItems.BLUE_GEODE_PICKAXE.value(), WRItems.BLUE_GEODE_AXE.value(), WRItems.BLUE_GEODE_SHOVEL.value(), WRItems.BLUE_GEODE_HOE.value());
        toolSet(output, WRItems.RED_GEODE.value(), WRItems.RED_GEODE_SWORD.value(), WRItems.RED_GEODE_PICKAXE.value(), WRItems.RED_GEODE_AXE.value(), WRItems.RED_GEODE_SHOVEL.value(), WRItems.RED_GEODE_HOE.value());
        toolSet(output, WRItems.PURPLE_GEODE.value(), WRItems.PURPLE_GEODE_SWORD.value(), WRItems.PURPLE_GEODE_PICKAXE.value(), WRItems.PURPLE_GEODE_AXE.value(), WRItems.PURPLE_GEODE_SHOVEL.value(), WRItems.PURPLE_GEODE_HOE.value());
        toolSet(output, WRItems.Tags.PLATINUM_INGOTS, WRItems.PLATINUM_SWORD.value(), WRItems.PLATINUM_PICKAXE.value(), WRItems.PLATINUM_AXE.value(), WRItems.PLATINUM_SHOVEL.value(), WRItems.PLATINUM_HOE.value());

        armorSet(output, WRItems.BLUE_GEODE.value(), WRItems.BLUE_GEODE_HELMET.value(), WRItems.BLUE_GEODE_CHESTPLATE.value(), WRItems.BLUE_GEODE_LEGGINGS.value(), WRItems.BLUE_GEODE_BOOTS.value());
        armorSet(output, WRItems.RED_GEODE.value(), WRItems.RED_GEODE_HELMET.value(), WRItems.RED_GEODE_CHESTPLATE.value(), WRItems.RED_GEODE_LEGGINGS.value(), WRItems.RED_GEODE_BOOTS.value());
        armorSet(output, WRItems.PURPLE_GEODE.value(), WRItems.PURPLE_GEODE_HELMET.value(), WRItems.PURPLE_GEODE_CHESTPLATE.value(), WRItems.PURPLE_GEODE_LEGGINGS.value(), WRItems.PURPLE_GEODE_BOOTS.value());
        armorSet(output, WRItems.Tags.PLATINUM_INGOTS, WRItems.PLATINUM_HELMET.value(), WRItems.PLATINUM_CHESTPLATE.value(), WRItems.PLATINUM_LEGGINGS.value(), WRItems.PLATINUM_BOOTS.value());

        shapeless(RecipeCategory.COMBAT, output, WRItems.DRAKE_HELMET.value(), new ShapelessPair(WRItems.DRAKE_BACKPLATE.value(), 3), new ShapelessPair(WRItems.PLATINUM_HELMET.value()));
        shapeless(RecipeCategory.COMBAT, output, WRItems.DRAKE_CHESTPLATE.value(), new ShapelessPair(WRItems.DRAKE_BACKPLATE.value(), 6), new ShapelessPair(WRItems.PLATINUM_CHESTPLATE.value()));
        shapeless(RecipeCategory.COMBAT, output, WRItems.DRAKE_LEGGINGS.value(), new ShapelessPair(WRItems.DRAKE_BACKPLATE.value(), 5), new ShapelessPair(WRItems.PLATINUM_LEGGINGS.value()));
        shapeless(RecipeCategory.COMBAT, output, WRItems.DRAKE_BOOTS.value(), new ShapelessPair(WRItems.DRAKE_BACKPLATE.value(), 2), new ShapelessPair(WRItems.PLATINUM_BOOTS.value()));

        // Food
        smelt(output, WRItems.LDWYRM.value(), WRItems.COOKED_MINUTUS.value(), 0.35f, 200, true);
        smelt(output, WRItems.RAW_LOWTIER_MEAT.value(), WRItems.COOKED_LOWTIER_MEAT.value(), 0.35f, 150, true);
        smelt(output, WRItems.RAW_COMMON_MEAT.value(), WRItems.COOKED_COMMON_MEAT.value(), 0.35f, 200, true);
        smelt(output, WRItems.RAW_APEX_MEAT.value(), WRItems.COOKED_APEX_MEAT.value(), 0.35f, 200, true);
        smelt(output, WRItems.RAW_BEHEMOTH_MEAT.value(), WRItems.COOKED_BEHEMOTH_MEAT.value(), 0.5f, 250, true);
        shaped(RecipeCategory.FOOD, WRItems.JEWELLED_APPLE.value()).define('A', Items.APPLE).define('G', WRItems.Tags.GEODES).pattern(" G ").pattern("GAG").pattern(" G ").unlockedBy("has_geode", has(WRItems.BLUE_GEODE.value())).save(output);

        // Dragon armor
        shaped(RecipeCategory.COMBAT, WRItems.DRAGON_ARMOR_IRON.value()).define('X', Items.IRON_INGOT).define('#', Items.IRON_BLOCK).pattern("X# ").pattern("X #").pattern(" X ").unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);
        shaped(RecipeCategory.COMBAT, WRItems.DRAGON_ARMOR_GOLD.value()).define('X', Items.GOLD_INGOT).define('#', Items.GOLD_BLOCK).pattern("X# ").pattern("X #").pattern(" X ").unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(output);
        shaped(RecipeCategory.COMBAT, WRItems.DRAGON_ARMOR_DIAMOND.value()).define('X', Items.DIAMOND).define('#', Items.DIAMOND_BLOCK).pattern("X# ").pattern("X #").pattern(" X ").unlockedBy("has_diamond", has(Items.DIAMOND)).save(output);
        shaped(RecipeCategory.COMBAT, WRItems.DRAGON_ARMOR_PLATINUM.value()).define('X', WRItems.PLATINUM_INGOT.value()).define('#', WRBlocks.PLATINUM_BLOCK.value()).pattern("X# ").pattern("X #").pattern(" X ").unlockedBy("has_platinum", has(WRItems.PLATINUM_INGOT.value())).save(output);
        shaped(RecipeCategory.COMBAT, WRItems.DRAGON_ARMOR_BLUE_GEODE.value()).define('X', WRItems.BLUE_GEODE.value()).define('#', WRBlocks.BLUE_GEODE_BLOCK.value()).pattern("X# ").pattern("X #").pattern(" X ").unlockedBy("has_blue_geode", has(WRItems.BLUE_GEODE.value())).save(output);
        shaped(RecipeCategory.COMBAT, WRItems.DRAGON_ARMOR_RED_GEODE.value()).define('X', WRItems.RED_GEODE.value()).define('#', WRBlocks.RED_GEODE_BLOCK.value()).pattern("X# ").pattern("X #").pattern(" X ").unlockedBy("has_red_geode", has(WRItems.RED_GEODE.value())).save(output);
        shaped(RecipeCategory.COMBAT, WRItems.DRAGON_ARMOR_PURPLE_GEODE.value()).define('X', WRItems.PURPLE_GEODE.value()).define('#', WRBlocks.PURPLE_GEODE_BLOCK.value()).pattern("X# ").pattern("X #").pattern(" X ").unlockedBy("has_purple_geode", has(WRItems.PURPLE_GEODE.value())).save(output);
    }

    private static class ShapelessPair
    {
        ItemLike item;
        int count;

        public ShapelessPair(ItemLike item, int count)
        {
            this.item = item;
            this.count = count;
        }

        public ShapelessPair(ItemLike item)
        {
            this.item = item;
            this.count = 1;
        }
    }
}
