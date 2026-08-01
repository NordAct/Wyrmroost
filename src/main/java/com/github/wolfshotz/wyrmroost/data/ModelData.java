package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.items.CoinDragonItem;
import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ModelData
{
    static void provide(DataGenerator gen, boolean include, ExistingFileHelper fileHelper, PackOutput output) {

        gen.addProvider(include, new Blocks(output, fileHelper));
        gen.addProvider(include, new Items(output, fileHelper));
        gen.addProvider(include, new BlockStates(output, fileHelper));
    }

    private static class Blocks extends BlockModelProvider {
        private final List<String> MISSING_TEXTURES = new ArrayList<>();

        Blocks(PackOutput output, ExistingFileHelper existingFileHelper)
        {
            super(output, Wyrmroost.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            // All unregistered blocks will be done here. They will be simple blocks with all sides of the same texture
            // If this is unwanted, it is important to define so above
            for (Block block : WRBlocks.REGISTRY.getEntries().stream().map(DeferredHolder::value).toList()) {
                if (block instanceof LiquidBlock) continue;

                ResourceLocation name = block.builtInRegistryHolder().key().location();
                ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
                if (!existingFileHelper.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures"))
                    MISSING_TEXTURES.add(name.getPath().replace("block/", ""));
                else cubeAll(block.builtInRegistryHolder().key().location().getPath(), texture);
            }

            if (!MISSING_TEXTURES.isEmpty())
                Wyrmroost.LOG.error("Blocks are missing Textures! Models will not be registered: {}", MISSING_TEXTURES.toString());
        }
    }

    private static class Items extends ItemModelProvider {
        private final List<Item> REGISTERED = new ArrayList<>();

        Items(PackOutput output, ExistingFileHelper existingFileHelper)
        {
            super(output, Wyrmroost.MOD_ID, existingFileHelper);
        }

        private static ResourceLocation resource(String path)
        {
            return Wyrmroost.rl("item/" + path);
        }

        private ItemModelBuilder item(Item item)
        {
            ItemModelBuilder builder = itemBare(item);

            // model
            String parent = (item instanceof TieredItem)? "item/handheld" : "item/generated";
            builder.parent(new ModelFile.UncheckedModelFile(parent));

            // texture
            ResourceLocation texture = resource(item.builtInRegistryHolder().key().location().getPath());
            if (existingFileHelper.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures"))
                builder.texture("layer0", texture);
            else
                Wyrmroost.LOG.warn("Missing Texture for Item: {} , model will not be registered.", texture.getPath().replace("item/", ""));

            return builder;
        }

        private ItemModelBuilder itemBare(Item item)
        {
            REGISTERED.add(item);
            return getBuilder(item.builtInRegistryHolder().key().location().getPath());
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected void registerModels()
        {
            // path constants
            final ResourceLocation itemGenerated = mcLoc("item/generated");
            final ResourceLocation spawnEggTemplate = mcLoc("item/template_spawn_egg");

            itemBare(WRItems.DRAGON_EGG.value())
                    .parent(uncheckedModel("builtin/entity"))
                    .guiLight(BlockModel.GuiLight.FRONT)
                    .transforms()
                    .transform(ItemDisplayContext.GUI).rotation(160, 8, 30).translation(21, 6, 0).scale(1.5f).end()
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(180, 10, 4).translation(13, 10, -10).scale(1).end()
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(180, 10, 4).translation(-2, 11, -12).scale(1).end()
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(253, 65, 0).translation(8, 2, 10).scale(0.75f).end()
                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(253, 65, 0).translation(3, 13, 7).scale(0.75f).end()
                    .transform(ItemDisplayContext.GROUND).rotation(180, 0, 0).translation(4, 8, -5).scale(0.55f).end();

            getBuilder("desert_wyrm_alive")
                    .parent(uncheckedModel(itemGenerated))
                    .texture("layer0", resource("desert_wyrm_alive"));
            item(WRItems.LDWYRM.value())
                    .override()
                    .predicate(Wyrmroost.rl("is_alive"), 1f)
                    .model(uncheckedModel(resource("desert_wyrm_alive")));

            item(WRItems.DRAGON_STAFF.value()).parent(uncheckedModel("item/handheld"));

            final ItemModelBuilder cdBuilder = item(WRItems.COIN_DRAGON.value());
            for (int i = 1; i < 5; i++)
            {
                String path = "coin_dragon" + i;
                getBuilder(path)
                        .parent(uncheckedModel(itemGenerated))
                        .texture("layer0", resource(path));
                cdBuilder.override()
                        .predicate(CoinDragonItem.VARIANT_OVERRIDE, i)
                        .model(uncheckedModel(resource(path)));
            }

            // spawn eggs
            for (Item e : WRItems.REGISTRY.getEntries().stream().filter(item -> item.value() instanceof SpawnEggItem).map(DeferredHolder::value).toList())
                itemBare(e).parent(uncheckedModel(spawnEggTemplate));

            // All standard item blocks
            for (Block block : WRBlocks.REGISTRY.getEntries().stream().map(DeferredHolder::value).toList())
            {
                if (REGISTERED.contains(block.asItem())) continue;
                ResourceLocation path = block.builtInRegistryHolder().key().location();
                itemBare(block.asItem()).parent(uncheckedModel(path.getNamespace() + ":block/" + path.getPath()));
            }

            // All items that do not require custom attention
            for (Item item : WRItems.REGISTRY.getEntries().stream().map(DeferredHolder::value).toList()) if (!REGISTERED.contains(item)) item(item);
        }

        @Override
        public String getName()
        {
            return "Wyrmroost Item Models";
        }

        private static ModelFile.UncheckedModelFile uncheckedModel(ResourceLocation path)
        {
            return new ModelFile.UncheckedModelFile(path);
        }

        private static ModelFile.UncheckedModelFile uncheckedModel(String path)
        {
            return new ModelFile.UncheckedModelFile(path);
        }
    }

    private static class BlockStates extends BlockStateProvider {

        public BlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
            super(output, Wyrmroost.MOD_ID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            simpleBlock(WRBlocks.PLATINUM_BLOCK.value());
            simpleBlock(WRBlocks.CHISELED_PLATINUM_BLOCK.value());
            simpleBlock(WRBlocks.PLATINUM_ORE.value());
            simpleBlock(WRBlocks.RAW_PLATINUM_BLOCK.value());
            simpleBlock(WRBlocks.PURPLE_GEODE_ORE.value());
            simpleBlock(WRBlocks.PURPLE_GEODE_BLOCK.value());
            simpleBlock(WRBlocks.RED_GEODE_ORE.value());
            simpleBlock(WRBlocks.RED_GEODE_BLOCK.value());
            simpleBlock(WRBlocks.BLUE_GEODE_ORE.value());
            simpleBlock(WRBlocks.BLUE_GEODE_BLOCK.value());
        }
    }
}
