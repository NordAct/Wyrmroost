package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.wolfshotz.wyrmroost.registry.WRBlocks.*;

class LootTableData extends LootTableProvider
{
    LootTableData(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(
                output,
                Set.of(),
                ImmutableList.of(
                        new LootTableProvider.SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(Entities::new, LootContextParamSets.ENTITY)
                ),
                provider);
    }

    private static class Blocks extends BlockLootSubProvider {
        public final Map<Block, LootTable.Builder> lootTables = new HashMap<>();

        protected Blocks(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected void generate() {
            registerOre(BLUE_GEODE_ORE.value(), WRItems.BLUE_GEODE.value());
            registerOre(RED_GEODE_ORE.value(), WRItems.RED_GEODE.value());
            registerOre(PURPLE_GEODE_ORE.value(), WRItems.PURPLE_GEODE.value());
            registerOre(PLATINUM_ORE.value(), WRItems.RAW_PLATINUM.value());

            for (Block block : getKnownBlocks()) // All blocks that have not been given special treatment above, drop themselves!
            {
                if (!lootTables.containsKey(block) && block.getLootTable() != BuiltInLootTables.EMPTY) // Loottable is already set to not have one, ignore.
                    dropSelf(block);
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return WRBlocks.REGISTRY.getEntries().stream().map(DeferredHolder::value).collect(Collectors.toList());
        }

        private void registerOre(Block ore, Item output) { add(ore, block -> createOreDrop(block, output)); }

        @Override
        protected void add(Block blockIn, LootTable.Builder table) {
            super.add(blockIn, table);
            lootTables.put(blockIn, table);
        }
    }

    private static class Entities extends EntityLootSubProvider {
        private static final LootItemConditionalFunction.Builder<?> ON_FIRE_SMELT = SmeltItemFunction.smelted()
                .when(
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true)).build()
                        )
                );

        private final Map<EntityType<?>, LootTable.Builder> lootTables = new HashMap<>();

        protected Entities(HolderLookup.Provider registries) {
            super(FeatureFlags.REGISTRY.allFlags(), FeatureFlags.REGISTRY.subset(), registries);
        }

        /**
         * Our way is much neater and cooler anyway. fuck mojang
         */
        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder>  consumer)
        {
            generate();
            getKnownEntityTypes().forEach(entity -> {
                if (!lootTables.containsKey(entity))
                {
                    if (entity.getCategory() == MobCategory.MISC) return;
                    throw new IllegalArgumentException(String.format("Missing Loottable for entry: '%s'", EntityType.getKey(entity)));
                }
                consumer.accept(entity.getDefaultLootTable(), lootTables.remove(entity));
            });
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes()
        {
            return WREntities.REGISTRY.getEntries().stream().map(DeferredHolder::value);
        }

        /**
         * @param types the types to register an empty loot tables
         * @deprecated SHOULD ONLY USE THIS WHEN AN ENTITY ABSOLUTELY DOES NOT HAVE ONE, OR IN TESTING!
         */
        @Deprecated
        public void registerEmptyTables(EntityType<?>... types)
        {
            for (EntityType<?> type : types)
            {
                Wyrmroost.LOG.warn("Registering EMPTY Loottable for: '{}'", EntityType.getKey(type));
                add(type, LootTable.lootTable());
            }
        }

        @Override
        protected void add(EntityType<?> type, LootTable.Builder table)
        {
            lootTables.put(type, table);
        }

        @Override
        public void generate()
        {
            add(WREntities.LESSER_DESERTWYRM.value(), LootTable.lootTable().withPool(singleRollPool().add(item(WRItems.LDWYRM.value(), 1).apply(ON_FIRE_SMELT))));

            add(WREntities.OVERWORLD_DRAKE.value(), LootTable.lootTable()
                    .withPool(singleRollPool().add(item(Items.LEATHER, 5, 10)).apply(looting(registries, 1, 4)))
                    .withPool(singleRollPool().add(meat(registries, WRItems.RAW_COMMON_MEAT.value(), 1, 7, 2, 3)))
                    .withPool(singleRollPool().add(item(WRItems.DRAKE_BACKPLATE.value(), 1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceCondition.randomChance(0.15f)).apply(looting(registries, 0, 1))));

            add(WREntities.ROOSTSTALKER.value(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(registries, WRItems.RAW_LOWTIER_MEAT.value(), 0, 2, 1, 2)))
                    .withPool(singleRollPool().add(item(Items.GOLD_NUGGET, 0, 2))));

            add(WREntities.DRAGON_FRUIT_DRAKE.value(), LootTable.lootTable().withPool(singleRollPool().add(item(Items.APPLE, 0, 6))));

            add(WREntities.CANARI_WYVERN.value(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(registries, WRItems.RAW_COMMON_MEAT.value(), 0, 2, 1, 2)))
                    .withPool(singleRollPool().add(item(Items.FEATHER, 1, 4).apply(looting(registries, 2, 6)))));

            add(WREntities.SILVER_GLIDER.value(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(registries, WRItems.RAW_LOWTIER_MEAT.value(), 0, 3, 1, 3))));

            add(WREntities.BUTTERFLY_LEVIATHAN.value(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(registries, WRItems.RAW_APEX_MEAT.value(), 6, 10, 2, 4)))
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 4)).add(item(Items.SEA_PICKLE, 0, 2).apply(looting(registries, 1, 2))).add(item(Items.SEAGRASS, 4, 14)).add(item(Items.KELP, 16, 24)))
                    .withPool(singleRollPool().add(item(Items.HEART_OF_THE_SEA, 1).when(LootItemRandomChanceCondition.randomChance(0.1f))).add(item(Items.NAUTILUS_SHELL, 1).when(LootItemRandomChanceCondition.randomChance(0.15f)))));

            add(WREntities.ROYAL_RED.value(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(registries, WRItems.RAW_APEX_MEAT.value(), 4, 8, 3, 5))));

            add(WREntities.COIN_DRAGON.value(), LootTable.lootTable().withPool(singleRollPool()
                    .add(meat(registries, WRItems.RAW_LOWTIER_MEAT.value(), 1, 1, 0, 1))
                    .add(item(Items.GOLD_NUGGET, 4))));

            add(WREntities.ALPINE.value(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(registries, WRItems.RAW_COMMON_MEAT.value(), 3, 7, 2, 6)))
                    .withPool(
                            singleRollPool()
                                    .add(item(Items.FEATHER, 3, 10)).apply(looting(registries, 3, 11))
                    )
            )
            ;
        }

        private static LootItemFunction.Builder looting(HolderLookup.Provider registries, float min, float max)
        {
            return EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(min, max));
        }

        private static LootPoolSingletonContainer.Builder<?> item(ItemLike itemIn, float minIn, float maxIn)
        {
            return LootItem.lootTableItem(itemIn).apply(SetItemCountFunction.setCount(UniformGenerator.between(minIn, maxIn)));
        }

        private static LootPoolSingletonContainer.Builder<?> item(ItemLike itemIn, int amount)
        {
            return LootItem.lootTableItem(itemIn).apply(SetItemCountFunction.setCount(ConstantValue.exactly(amount)));
        }

        private static LootPool.Builder singleRollPool()
        {
            return LootPool.lootPool().setRolls(ConstantValue.exactly(1));
        }

        private static LootPoolEntryContainer.Builder<?> meat(HolderLookup.Provider registries, ItemLike itemIn, int minAmount, int maxAmount, int lootingMin, int lootingMax)
        {
            return item(itemIn, minAmount, maxAmount).apply(ON_FIRE_SMELT).apply(looting(registries, lootingMin, lootingMax));
        }
    }
}
