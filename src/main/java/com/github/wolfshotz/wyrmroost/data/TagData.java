package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TagData
{
    // note block tags need to run before item tags
    static void provide(DataGenerator gen, boolean add, ExistingFileHelper fileHelper, PackOutput output, CompletableFuture<HolderLookup.Provider> provider)
    {
        BlockData blockGen = new BlockData(output, provider, fileHelper);
        gen.addProvider(add, blockGen);
        gen.addProvider(add, new ItemData(output, provider, blockGen.contentsGetter(), fileHelper));
        gen.addProvider(add, new EntityData(output, provider, fileHelper));
        gen.addProvider(add, new DamageTypeData(output, provider, fileHelper));
        gen.addProvider(add, new BiomeData(output, provider, fileHelper));
    }

    private static class ItemData extends ItemTagsProvider
    {
        public ItemData(PackOutput output, CompletableFuture<HolderLookup.Provider> provider,
                        CompletableFuture<TagLookup<Block>> blockData, ExistingFileHelper fileHelper)
        {
            super(output, provider, blockData, Wyrmroost.MOD_ID, fileHelper );
        }

        @Override
        protected void addTags(HolderLookup.Provider var1) {
            WRBlocks.Tags.ITEM_BLOCK_TAGS.forEach(this::copy);

            tag(Tags.Items.EGGS).add(WRItems.DRAGON_EGG.value());

            tag(ItemTags.PIGLIN_LOVED).add(WRItems.DRAGON_ARMOR_GOLD.value()); // PIGLIN_LOVED

            tag(Tags.Items.GEMS).addTag(WRItems.Tags.GEODES);
            tag(WRItems.Tags.GEODES).add(WRItems.BLUE_GEODE.value(), WRItems.RED_GEODE.value(), WRItems.PURPLE_GEODE.value());

            tag(WRItems.Tags.DRAGON_MEATS).add(WRItems.RAW_LOWTIER_MEAT.value(), WRItems.COOKED_LOWTIER_MEAT.value(), WRItems.RAW_COMMON_MEAT.value(), WRItems.COOKED_COMMON_MEAT.value(), WRItems.RAW_APEX_MEAT.value(), WRItems.COOKED_APEX_MEAT.value(), WRItems.RAW_BEHEMOTH_MEAT.value(), WRItems.COOKED_BEHEMOTH_MEAT.value());
            tag(ItemTags.MEAT).addTag(WRItems.Tags.DRAGON_MEATS);
            tag(Tags.Items.FOODS_RAW_MEAT)
                    .add(
                            WRItems.RAW_LOWTIER_MEAT.value(),
                            WRItems.RAW_COMMON_MEAT.value(),
                            WRItems.RAW_APEX_MEAT.value(),
                            WRItems.RAW_BEHEMOTH_MEAT.value()

                            )
                    .add(WRItems.LDWYRM.value());
            tag(Tags.Items.FOODS_COOKED_MEAT)
                    .add(
                            WRItems.COOKED_LOWTIER_MEAT.value(),
                            WRItems.COOKED_COMMON_MEAT.value(),
                            WRItems.COOKED_APEX_MEAT.value(),
                            WRItems.COOKED_BEHEMOTH_MEAT.value()
                    )
                    .add(WRItems.COOKED_MINUTUS.value());

            tag(Tags.Items.INGOTS).addTag(WRItems.Tags.PLATINUM_INGOTS);
            tag(WRItems.Tags.PLATINUM_INGOTS).add(WRItems.PLATINUM_INGOT.value());

            tag(WRItems.Tags.GEODE_TIPPED_ARROWS).add(WRItems.BLUE_GEODE_ARROW.value(), WRItems.RED_GEODE_ARROW.value(), WRItems.PURPLE_GEODE_ARROW.value());
            tag(ItemTags.ARROWS).addTag(WRItems.Tags.GEODE_TIPPED_ARROWS);

            tag(ItemTags.BEACON_PAYMENT_ITEMS).addTag(WRItems.Tags.GEODES);

            tag(WRItems.Tags.ALPINE_FOOD)
                    .add(Items.HONEYCOMB)
                    .add(Items.HONEY_BOTTLE)
            ;
            tag(WRItems.Tags.BUTTERFLY_LEVIATHAN_FOOD)
                    .addTag(ItemTags.MEAT)
                    .addTag(Tags.Items.FOODS_COOKED_MEAT)
                    .addTag(Tags.Items.FOODS_RAW_MEAT)
            ;
            tag(WRItems.Tags.CANARI_WYVERN_FOOD).add(Items.SWEET_BERRIES);
            tag(WRItems.Tags.DRAGON_FRUIT_DRAKE_FOOD).add(Items.APPLE);
            tag(WRItems.Tags.OVERWORLD_DRAKE_FOOD).addTag(Tags.Items.CROPS_WHEAT);
            tag(WRItems.Tags.ROOST_STALKER_FOOD)
                    .addTag(ItemTags.MEAT)
                    .addTag(Tags.Items.FOODS_COOKED_MEAT)
                    .addTag(Tags.Items.FOODS_RAW_MEAT)
            ;
            tag(WRItems.Tags.ROYAL_RED_FOOD)
                    .addTag(ItemTags.MEAT)
                    .addTag(Tags.Items.FOODS_COOKED_MEAT)
                    .addTag(Tags.Items.FOODS_RAW_MEAT)
            ;
            tag(WRItems.Tags.SILVER_GLIDER_FOOD)
                    .addTag(ItemTags.FISHES)
                    .addTag(Tags.Items.FOODS_COOKED_FISH)
                    .addTag(Tags.Items.FOODS_RAW_FISH)
            ;

            tag(WRItems.Tags.ALPINE_BREEDING_ITEMS).addTag(WRItems.Tags.ALPINE_FOOD);
            tag(WRItems.Tags.BUTTERFLY_LEVIATHAN_BREEDING_ITEMS).add(Items.KELP);
            tag(WRItems.Tags.CANARI_WYVERN_BREEDING_ITEMS).addTag(WRItems.Tags.CANARI_WYVERN_FOOD);
            tag(WRItems.Tags.DRAGON_FRUIT_DRAKE_BREEDING_ITEMS).addTag(WRItems.Tags.DRAGON_FRUIT_DRAKE_FOOD);
            tag(WRItems.Tags.OVERWORLD_DRAKE_BREEDING_ITEMS).addTag(WRItems.Tags.OVERWORLD_DRAKE_FOOD);
            tag(WRItems.Tags.ROOST_STALKER_BREEDING_ITEMS).addTag(Tags.Items.NUGGETS_GOLD);
            tag(WRItems.Tags.ROYAL_RED_BREEDING_ITEMS).addTag(WRItems.Tags.ROYAL_RED_FOOD);
            tag(WRItems.Tags.SILVER_GLIDER_BREEDING_ITEMS).addTag(WRItems.Tags.SILVER_GLIDER_FOOD);

            tag(WRItems.Tags.ACTIVATES_DRAGON_FRUIT_DRAKE_CROPS_GROWTH).add(Items.GLISTERING_MELON_SLICE);
            tag(WRItems.Tags.ROOST_STALKER_TAMING_ITEMS)
                    .addTag(Tags.Items.EGGS);

            tag(ItemTags.PICKAXES)
                    .add(WRItems.BLUE_GEODE_PICKAXE.value())
                    .add(WRItems.RED_GEODE_PICKAXE.value())
                    .add(WRItems.PURPLE_GEODE_PICKAXE.value())
                    .add(WRItems.PLATINUM_PICKAXE.value())
            ;

            tag(ItemTags.SHOVELS)
                    .add(WRItems.BLUE_GEODE_SHOVEL.value())
                    .add(WRItems.RED_GEODE_SHOVEL.value())
                    .add(WRItems.PURPLE_GEODE_SHOVEL.value())
                    .add(WRItems.PLATINUM_SHOVEL.value())
            ;

            tag(ItemTags.SWORDS)
                    .add(WRItems.BLUE_GEODE_SWORD.value())
                    .add(WRItems.RED_GEODE_SWORD.value())
                    .add(WRItems.PURPLE_GEODE_SWORD.value())
                    .add(WRItems.PLATINUM_SWORD.value())
            ;

            tag(ItemTags.HOES)
                    .add(WRItems.BLUE_GEODE_HOE.value())
                    .add(WRItems.RED_GEODE_HOE.value())
                    .add(WRItems.PURPLE_GEODE_HOE.value())
                    .add(WRItems.PLATINUM_HOE.value())
            ;

            tag(ItemTags.AXES)
                    .add(WRItems.BLUE_GEODE_AXE.value())
                    .add(WRItems.RED_GEODE_AXE.value())
                    .add(WRItems.PURPLE_GEODE_AXE.value())
                    .add(WRItems.PLATINUM_AXE.value())
            ;

            tag(ItemTags.HEAD_ARMOR)
                    .add(WRItems.BLUE_GEODE_HELMET.value())
                    .add(WRItems.RED_GEODE_HELMET.value())
                    .add(WRItems.PURPLE_GEODE_HELMET.value())
                    .add(WRItems.PLATINUM_HELMET.value())
                    .add(WRItems.DRAKE_HELMET.value())
            ;

            tag(ItemTags.CHEST_ARMOR)
                    .add(WRItems.BLUE_GEODE_CHESTPLATE.value())
                    .add(WRItems.RED_GEODE_CHESTPLATE.value())
                    .add(WRItems.PURPLE_GEODE_CHESTPLATE.value())
                    .add(WRItems.PLATINUM_CHESTPLATE.value())
                    .add(WRItems.DRAKE_CHESTPLATE.value())
            ;

            tag(ItemTags.LEG_ARMOR)
                    .add(WRItems.BLUE_GEODE_LEGGINGS.value())
                    .add(WRItems.RED_GEODE_LEGGINGS.value())
                    .add(WRItems.PURPLE_GEODE_LEGGINGS.value())
                    .add(WRItems.PLATINUM_LEGGINGS.value())
                    .add(WRItems.DRAKE_LEGGINGS.value())
            ;

            tag(ItemTags.FOOT_ARMOR)
                    .add(WRItems.BLUE_GEODE_BOOTS.value())
                    .add(WRItems.RED_GEODE_BOOTS.value())
                    .add(WRItems.PURPLE_GEODE_BOOTS.value())
                    .add(WRItems.PLATINUM_BOOTS.value())
                    .add(WRItems.DRAKE_BOOTS.value())
            ;
        }
    }

    private static class BlockData extends BlockTagsProvider
    {
        public BlockData(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper)
        {
            super(output, provider, Wyrmroost.MOD_ID, existingFileHelper);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void addTags(HolderLookup.Provider var1) {
            tag(BlockTags.BEACON_BASE_BLOCKS).addTag(WRBlocks.Tags.STORAGE_BLOCKS_GEODE);

            tag(Tags.Blocks.ORES).addTag(WRBlocks.Tags.ORES_GEODE).addTag(WRBlocks.Tags.ORES_PLATINUM);
            tag(WRBlocks.Tags.ORES_GEODE).add(WRBlocks.BLUE_GEODE_ORE.value(), WRBlocks.RED_GEODE_ORE.value(), WRBlocks.PURPLE_GEODE_ORE.value());
            tag(WRBlocks.Tags.ORES_PLATINUM).add(WRBlocks.PLATINUM_ORE.value());
            tag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK).add(WRBlocks.RED_GEODE_ORE.value());
            tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(WRBlocks.BLUE_GEODE_ORE.value(), WRBlocks.PLATINUM_ORE.value());

            tag(Tags.Blocks.STORAGE_BLOCKS).addTags(WRBlocks.Tags.STORAGE_BLOCKS_GEODE, WRBlocks.Tags.STORAGE_BLOCKS_PLATINUM);
            tag(WRBlocks.Tags.STORAGE_BLOCKS_GEODE);
            tag(WRBlocks.Tags.STORAGE_BLOCKS_PLATINUM).add(WRBlocks.PLATINUM_BLOCK.value());

            tag(BlockTags.DRAGON_IMMUNE).add(WRBlocks.PURPLE_GEODE_ORE.value());

            tag(WRBlocks.Tags.DRAGON_FRUIT_DRAKE_CAN_SPAWN_ON).add(Blocks.JUNGLE_LEAVES);

            tag(WRBlocks.Tags.LESSER_DESERT_WYRM_CAN_BURROW_IN).addTag(Tags.Blocks.SANDS);

            tag(BlockTags.NEEDS_STONE_TOOL)
                    .add(WRBlocks.PLATINUM_ORE.value())
                    .add(WRBlocks.PLATINUM_BLOCK.value())
            ;

            tag(BlockTags.NEEDS_IRON_TOOL)
                    .add(WRBlocks.BLUE_GEODE_ORE.value(), WRBlocks.RED_GEODE_ORE.value(), WRBlocks.PURPLE_GEODE_ORE.value())
                    .add(WRBlocks.BLUE_GEODE_BLOCK.value(), WRBlocks.RED_GEODE_BLOCK.value(), WRBlocks.PURPLE_GEODE_BLOCK.value())
            ;

            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(WRBlocks.PLATINUM_ORE.value())
                    .add(WRBlocks.PLATINUM_BLOCK.value())
                    .add(WRBlocks.BLUE_GEODE_ORE.value(), WRBlocks.RED_GEODE_ORE.value(), WRBlocks.PURPLE_GEODE_ORE.value())
                    .add(WRBlocks.BLUE_GEODE_BLOCK.value(), WRBlocks.RED_GEODE_BLOCK.value(), WRBlocks.PURPLE_GEODE_BLOCK.value())
            ;
        }
    }

    private static class EntityData extends EntityTypeTagsProvider
    {
        private EntityData(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
            super(output, provider, Wyrmroost.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider var1) {
            tag(WREntities.Tags.GEODE_ARROWS)
                    .add(WREntities.BLUE_GEODE_TIPPED_ARROW.value())
                    .add(WREntities.RED_GEODE_TIPPED_ARROW.value())
                    .add(WREntities.PURPLE_GEODE_TIPPED_ARROW.value())
            ;
            tag(EntityTypeTags.ARROWS).addTag(WREntities.Tags.GEODE_ARROWS);
            tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER).add(WREntities.BUTTERFLY_LEVIATHAN.value());
            tag(EntityTypeTags.AQUATIC).add(WREntities.BUTTERFLY_LEVIATHAN.value());
            tag(EntityTypeTags.DISMOUNTS_UNDERWATER)
                    .add(WREntities.ALPINE.value())
                    .add(WREntities.ROYAL_RED.value())
                    .add(WREntities.OVERWORLD_DRAKE.value())
            ;

            tag(WREntities.Tags.DRAGONS)
                    .add(WREntities.ALPINE.value())
                    .add(WREntities.BUTTERFLY_LEVIATHAN.value())
                    .add(WREntities.CANARI_WYVERN.value())
                    .add(WREntities.COIN_DRAGON.value())
                    .add(WREntities.DRAGON_FRUIT_DRAKE.value())
                    .add(WREntities.LESSER_DESERTWYRM.value())
                    .add(WREntities.OVERWORLD_DRAKE.value())
                    .add(WREntities.ROOSTSTALKER.value())
                    .add(WREntities.ROYAL_RED.value())
                    .add(WREntities.SILVER_GLIDER.value())
            ;

            tag(WREntities.Tags.SOUL_CRYSTAL_CAN_FIT)
                    .addTag(WREntities.Tags.DRAGONS)
                    .addOptional(ResourceLocation.fromNamespaceAndPath("iceandfire", "fire_dragon"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("iceandfire", "lightning_dragon"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("iceandfire", "ice_dragon"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("iceandfire", "amphithere"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("dmr", "dragon"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("uselessreptile", "wyvern"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("uselessreptile", "moleclaw"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("uselessreptile", "river_pikehorn"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("uselessreptile", "lightning_chaser"))
            ;

            tag(WREntities.Tags.HOME_DEFENDER_ATTACKABLE)
                    .addTag(EntityTypeTags.UNDEAD)
                    .addTag(EntityTypeTags.RAIDERS)
                    .addTag(EntityTypeTags.ILLAGER)
                    .addTag(EntityTypeTags.ARTHROPOD)
                    .remove(EntityType.BEE)
                    .remove(EntityType.SKELETON_HORSE)
                    .remove(EntityType.ZOMBIE_HORSE)
            ;

            tag(WREntities.Tags.BUTTERFLY_LEVIATHAN_CONDUIT_TARGETS)
                    .addTag(WREntities.Tags.HOME_DEFENDER_ATTACKABLE)
                    .addTag(EntityTypeTags.AQUATIC)
                    ;
        }
    }

    private static class DamageTypeData extends DamageTypeTagsProvider {

        public DamageTypeData(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, Wyrmroost.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider p_270108_) {
            tag(DamageTypeTags.IS_FIRE)
                    .addOptional(WRDamageTypes.FIRE_BREATH_0.location())
                    .addOptional(WRDamageTypes.FIRE_BREATH_1.location())
            ;
            tag(DamageTypeTags.IS_PROJECTILE)
                    .addOptional(WRDamageTypes.FIRE_BREATH_0.location())
                    .addOptional(WRDamageTypes.FIRE_BREATH_1.location())
                    .addOptional(WRDamageTypes.WIND_GUST.location())
            ;
        }
    }

    private static class BiomeData extends BiomeTagsProvider {

        public BiomeData(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, Wyrmroost.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider p_270108_) {
            tag(WRBiomes.Tags.ALPINE_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_MOUNTAIN_PEAK)
                    .add(Biomes.MEADOW)
            ;

            tag(WRBiomes.Tags.BUTTERFLY_LEVIATHAN_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_OCEAN);

            tag(WRBiomes.Tags.CANARI_WYVERN_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_SWAMP);

            tag(WRBiomes.Tags.DRAGON_FRUIT_DRAKE_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_JUNGLE);

            tag(WRBiomes.Tags.LESSER_DESERT_WYRM_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_DESERT);

            tag(WRBiomes.Tags.OVERWORLD_DRAKE_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_PLAINS)
                    .addTag(Tags.Biomes.IS_SAVANNA)
            ;

            tag(WRBiomes.Tags.ROOST_STALKER_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_PLAINS)
                    .addTag(Tags.Biomes.IS_MOUNTAIN_SLOPE)
                    .addTag(Tags.Biomes.IS_FOREST)
            ;

            tag(WRBiomes.Tags.ROYAL_RED_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_MOUNTAIN_PEAK);

            tag(WRBiomes.Tags.SILVER_GLIDER_CAN_SPAWN)
                    .addTag(Tags.Biomes.IS_OCEAN)
                    .addTag(Tags.Biomes.IS_BEACH)
                    .addTag(Tags.Biomes.IS_STONY_SHORES);
        }
    }
}