package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WRBlocks
{
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, Wyrmroost.MOD_ID);

    public static final Holder<Block> PLATINUM_ORE = register("platinum_ore", () -> new Block(builder().requiresCorrectToolForDrops().strength(3).sound(SoundType.STONE).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Holder<Block> PLATINUM_BLOCK = register("platinum_block", () -> new Block(builder().requiresCorrectToolForDrops().strength(5).sound(SoundType.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE)));

    public static final Holder<Block> BLUE_GEODE_ORE = register("blue_geode_ore", () -> new DropExperienceBlock(UniformInt.of(3, 7), builder().requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Holder<Block> BLUE_GEODE_BLOCK = register("blue_geode_block", () -> new Block(builder().requiresCorrectToolForDrops().sound(SoundType.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE)));
    public static final Holder<Block> RED_GEODE_ORE = register("red_geode_ore", () -> new DropExperienceBlock(UniformInt.of(4, 8), builder().requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Holder<Block> RED_GEODE_BLOCK = register("red_geode_block", () -> new Block(builder().requiresCorrectToolForDrops().sound(SoundType.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE)));
    public static final Holder<Block> PURPLE_GEODE_ORE = register("purple_geode_ore", () -> new DropExperienceBlock(UniformInt.of(8, 11), builder().requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Holder<Block> PURPLE_GEODE_BLOCK = register("purple_geode_block", () -> new Block(builder().requiresCorrectToolForDrops().sound(SoundType.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE)));

    public static Holder<Block> register(String name, Supplier<Block> block)
    {
        Holder<Block> blockHolder = REGISTRY.register(name, block);
        WRItems.register(name, () -> new BlockItem(blockHolder.value(), WRItems.builder()));
        return blockHolder;
    }

    public static Block.Properties builder() {
        return BlockBehaviour.Properties.of();
    }

    public static class Tags {
        public static final Map<TagKey<Block>, TagKey<Item>> ITEM_BLOCK_TAGS = new HashMap<>();

        public static final TagKey<Block> ORES_GEODE = blockItemTag("ores/geode");
        public static final TagKey<Block> ORES_PLATINUM = blockItemTag("ores/platinum");
        public static final TagKey<Block> STORAGE_BLOCKS_GEODE = blockItemTag("storage_blocks/geode");
        public static final TagKey<Block> STORAGE_BLOCKS_PLATINUM = blockItemTag("storage_blocks/platinum");

        public static final TagKey<Block> ALPINE_CAN_SPAWN_ON = tag("alpine_can_spawn_on");
        public static final TagKey<Block> BUTTERFLY_LEVIATHAN_CAN_SPAWN_ON = tag("butterfly_leviathan_can_spawn_on");
        public static final TagKey<Block> CANARI_WYVERN_CAN_SPAWN_ON = tag("canari_wyvern_can_spawn_on");
        public static final TagKey<Block> DRAGON_FRUIT_DRAKE_CAN_SPAWN_ON = tag("dragon_fruit_drake_can_spawn_on");
        public static final TagKey<Block> LESSER_DESERT_WYRM_CAN_SPAWN_ON = tag("lesser_desert_wyrm_can_spawn_on");
        public static final TagKey<Block> OVERWORLD_DRAKE_CAN_SPAWN_ON = tag("overworld_drake_can_spawn_on");
        public static final TagKey<Block> ROOST_STALKER_CAN_SPAWN_ON = tag("roost_stalker_can_spawn_on");
        public static final TagKey<Block> ROYAL_RED_CAN_SPAWN_ON = tag("roost_stalker_can_spawn_on");
        public static final TagKey<Block> SILVER_GLIDER_CAN_SPAWN_ON = tag("roost_stalker_can_spawn_on");

        public static TagKey<Block> blockItemTag(String path) {
            return getFor(Wyrmroost.MOD_ID + ":" + path);
        }


        public static TagKey<Block> tag(String path) {
            return TagKey.create(Registries.BLOCK, Wyrmroost.rl(path));
        }

        public static TagKey<Block> getFor(String path) {
            TagKey<Block> tag = BlockTags.create(ResourceLocation.parse(path));
            ITEM_BLOCK_TAGS.put(tag, ItemTags.create(ResourceLocation.parse(path)));
            return tag;
        }
    }
}