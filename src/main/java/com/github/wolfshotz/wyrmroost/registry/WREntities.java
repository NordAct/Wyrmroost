package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.CommonEvents;
import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.render.entity.alpine.AlpineRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.butterfly.ButterflyLeviathanRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.canari.CanariWyvernRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.coin_dragon.CoinDragonRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.dragon_egg.DragonEggRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.dragon_fruit.DragonFruitDrakeRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.ldwyrm.LDWyrmRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.owdrake.OWDrakeRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.projectile.BreathWeaponRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.projectile.GeodeTippedArrowRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.rooststalker.RoostStalkerRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.royal_red.RoyalRedRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.silverglider.SilverGliderRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.*;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggEntity;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.entities.projectile.WindGustEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.BlueGeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.GeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.PurpleGeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.RedGeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.breath.FireBreathEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by com.github.WolfShotz - 7/3/19 19:03 <p>
 * <p>
 * Class responsible for the setup and registration of entities, and their spawning.
 * Entity type generics are defined because a) forge told me so and b) because its broken without it.
 */
public class WREntities {
    public static final Collection<Pair<ResourceLocation, RegisterSpawnPlacementsEvent.MergedSpawnPredicate>> SPAWN_PREDICATES = new ArrayList<>();
    public static final Collection<Pair<ResourceLocation, Supplier<AttributeSupplier.Builder>>> ATTRIBUTES = new ArrayList<>();

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, Wyrmroost.MOD_ID);

    public static final Holder<EntityType<?>> LESSER_DESERTWYRM = Builder.creature("lesser_desertwyrm", LDWyrmEntity::new)
            .attributes(LDWyrmEntity::createAttributes)
            .spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LDWyrmEntity::getSpawnPlacement)
            .spawnEgg(0xD6BCBC, 0xDEB6C7)
            .renderer(() -> LDWyrmRenderer::new)
            .build(b -> b.sized(0.6f, 0.2f));

    public static final Holder<EntityType<?>> OVERWORLD_DRAKE = Builder.creature("overworld_drake", OWDrakeEntity::new)
            .attributes(OWDrakeEntity::createAttributes)
            .spawnPlacement()
            .spawnEgg(0x788716, 0x3E623E)
            .dragonEgg(new DragonEggProperties(0.65f, 1f, 18000))
            .renderer(() -> OWDrakeRenderer::new)
            .build(b -> b.sized(2.376f, 2.58f));

    public static final Holder<EntityType<?>> SILVER_GLIDER = Builder.creature("silver_glider", SilverGliderEntity::new)
            .attributes(SilverGliderEntity::createAttributes)
            .spawnPlacement(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SilverGliderEntity::getSpawnPlacement)
            .spawnEgg(0xC8C8C8, 0xC4C4C4)
            .dragonEgg(new DragonEggProperties(0.4f, 0.65f, 12000))
            .renderer(() -> SilverGliderRenderer::new)
            .build(b -> b.sized(1.5f, 0.75f));

    public static final Holder<EntityType<?>> ROOSTSTALKER = Builder.creature("roost_stalker", RoostStalkerEntity::new)
            .attributes(RoostStalkerEntity::createAttributes)
            .spawnPlacement()
            .spawnEgg(0x52100D, 0x959595)
            .dragonEgg(new DragonEggProperties(0.25f, 0.35f, 6000))
            .renderer(() -> RoostStalkerRenderer::new)
            .build(b -> b.sized(0.65f, 0.5f));

    public static final Holder<EntityType<?>> BUTTERFLY_LEVIATHAN = Builder.withClassification("butterfly_leviathan", ButterflyLeviathanEntity::new, MobCategory.WATER_CREATURE)
            .attributes(ButterflyLeviathanEntity::createAttributes)
            .spawnPlacement(SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ButterflyLeviathanEntity::getSpawnPlacement)
            .spawnEgg(0x17283C, 0x7A6F5A)
            .dragonEgg(new DragonEggProperties(0.75f, 1.25f, 40000).setConditions(Entity::isInWater))
            .renderer(() -> ButterflyLeviathanRenderer::new)
            .build(b -> b.sized(2.95f, 2.95f));

    public static final Holder<EntityType<?>> DRAGON_FRUIT_DRAKE = Builder.creature("dragon_fruit_drake", DragonFruitDrakeEntity::new)
            .attributes(DragonFruitDrakeEntity::createAttributes)
            .spawnPlacement(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DragonFruitDrakeEntity::getSpawnPlacement)
            .spawnEgg(0xe05c9a, 0x788716)
            .dragonEgg(new DragonEggProperties(0.45f, 0.75f, 9600))
            .renderer(() -> DragonFruitDrakeRenderer::new)
            .build(b -> b.sized(1.5f, 1.9f));

    public static final Holder<EntityType<?>> CANARI_WYVERN = Builder.creature("canari_wyvern", CanariWyvernEntity::new)
            .attributes(CanariWyvernEntity::createAttributes)
            .spawnPlacement(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, AbstractDragonEntity::canFlyerSpawn)
            .spawnEgg(0x1D1F28, 0x492E0E)
            .dragonEgg(new DragonEggProperties(0.25f, 0.35f, 6000).setConditions(c -> c.level().getBlockState(c.blockPosition().below()).is(WRBlocks.Tags.DRAGON_FRUIT_DRAKE_CAN_SPAWN_ON)))
            .renderer(() -> CanariWyvernRenderer::new)
            .build(b -> b.sized(0.65f, 0.85f));

    public static final Holder<EntityType<?>> ROYAL_RED = Builder.creature("royal_red", RoyalRedEntity::new)
            .attributes(RoyalRedEntity::createAttributes)
            .spawnPlacement(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, AbstractDragonEntity::canFlyerSpawn)
            .spawnEgg(0x8a0900, 0x0)
            .dragonEgg(new DragonEggProperties(0.6f, 1f, 72000))
            .renderer(() -> RoyalRedRenderer::new)
            .build(b -> b.sized(2.95f, 2.95f).fireImmune());

    public static final Holder<EntityType<?>> COIN_DRAGON = Builder.creature("coin_dragon", CoinDragonEntity::new)
            .renderer(() -> CoinDragonRenderer::new)
            .attributes(CoinDragonEntity::createAttributes)
            .build(b -> b.sized(0.35f, 0.435f));

    public static final Holder<EntityType<?>> ALPINE = Builder.creature("alpine", AlpineEntity::new)
            .attributes(AlpineEntity::createAttributes)
            .spawnPlacement(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, AbstractDragonEntity::canFlyerSpawn)
            .spawnEgg(0xe3f8ff, 0xa8e9ff)
            .dragonEgg(new DragonEggProperties(1, 1, 12000))
            .renderer(() -> AlpineRenderer::new)
            .build(b -> b.sized(2f, 2f));

//    public static final Holder<EntityType<OrbwyrmEntity>> ORBWYRM = Builder.creature("orbwyrm", OrbwyrmEntity::new)
//            .attributes(OrbwyrmEntity::createAttributes)
////            .spawnPlacement()
////            .spawnBiomes(OWDrakeEntity::setSpawnBiomes)
//            .spawnEgg(0x41444F, 0x16171C)
////            .dragonEgg(new DragonEggProperties(0.65f, 1f, 18000))
//            .renderer(() -> OrbwyrmRenderer::new)
//            .build(b -> b.sized(2.8f, 3.76f));

    public static final Holder<EntityType<?>> DRAGON_EGG = Builder.<DragonEggEntity>withClassification("dragon_egg", DragonEggEntity::new, MobCategory.MISC)
            .renderer(() -> DragonEggRenderer::new)
            .build(EntityType.Builder::noSummon);

    public static final Holder<EntityType<?>> BLUE_GEODE_TIPPED_ARROW = Builder.<GeodeTippedArrowEntity>withClassification("blue_geode_tipped_arrow", BlueGeodeTippedArrowEntity::new, MobCategory.MISC)
            .renderer(() -> GeodeTippedArrowRenderer::new)
            .build(b -> b.sized(0.5f, 0.5f));

    public static final Holder<EntityType<?>> RED_GEODE_TIPPED_ARROW = Builder.<GeodeTippedArrowEntity>withClassification("red_geode_tipped_arrow", RedGeodeTippedArrowEntity::new, MobCategory.MISC)
            .renderer(() -> GeodeTippedArrowRenderer::new)
            .build(b -> b.sized(0.5f, 0.5f));

    public static final Holder<EntityType<?>> PURPLE_GEODE_TIPPED_ARROW = Builder.<GeodeTippedArrowEntity>withClassification("purple_geode_tipped_arrow", PurpleGeodeTippedArrowEntity::new, MobCategory.MISC)
            .renderer(() -> GeodeTippedArrowRenderer::new)
            .build(b -> b.sized(0.5f, 0.5f));

    public static final Holder<EntityType<?>> FIRE_BREATH = Builder.<FireBreathEntity>withClassification("fire_breath", FireBreathEntity::new, MobCategory.MISC)
            .renderer(() -> BreathWeaponRenderer::new)
            .build(b -> b.sized(0.75f, 0.75f).noSave().noSummon());

    public static final Holder<EntityType<?>> WIND_GUST = Builder.<WindGustEntity>withClassification("wind_gust", WindGustEntity::new, MobCategory.MISC)
            .renderer(() -> NoopRenderer::new)
            .build(b -> b.sized(4, 4).noSave().noSummon());

//    public static final Holder<EntityType<SilkProjectileEntity>> SILK = Builder.withClassification("silk", SilkProjectileEntity::new, MobCategory.MISC)
//            .renderer(() -> EmptyRenderer::new) // todo
//            .build(b -> b.sized(0.5f, 0.5f).disableSerialization().disableSummoning());

    @SuppressWarnings("unchecked")
    private static class Builder<T extends Entity>
    {
        private final String name;
        private final EntityType.EntityFactory<T> factory;
        private final MobCategory classification;
        private Holder<EntityType<?>> registered;

        public Builder(String name, EntityType.EntityFactory<T> factory, MobCategory classification)
        {
            this.name = name;
            this.factory = factory;
            this.classification = classification;
        }

        private Builder<T> spawnEgg(int primColor, int secColor)
        {
            WRItems.register(name + "_spawn_egg", () -> new DeferredSpawnEggItem(() -> (EntityType<? extends Mob>) registered.value(), primColor, secColor, WRItems.builder()) {
                @Override
                public Component getName(ItemStack stack)
                {
                    ResourceLocation regName = EntityType.getKey(getDefaultType());
                    return Component.translatable("entity." + regName.getNamespace() + "." + regName.getPath())
                            .append(" ")
                            .append(Component.translatable("item.wyrmroost.spawn_egg"));
                }
            });
            return this;
        }

        private Builder<T> renderer(Supplier<Function<EntityRendererProvider.Context, EntityRenderer>> renderFactory)
        {
            if (FMLLoader.getDist() == Dist.CLIENT) {
                ClientEvents.RENDERERS.add(new Pair<>(Wyrmroost.rl(name), renderFactory.get()));
            }
            return this;
        }

        private Builder<T> attributes(Supplier<AttributeSupplier.Builder> supplier)
        {
            ATTRIBUTES.add(new Pair<>(Wyrmroost.rl(name), supplier));
            return this;
        }

        private <F extends Mob> Builder<T> spawnPlacement(SpawnPlacementType type, Heightmap.Types height, SpawnPlacements.SpawnPredicate<F> predicate)
        {
            SPAWN_PREDICATES.add(new Pair<>(Wyrmroost.rl(name), new RegisterSpawnPlacementsEvent.MergedSpawnPredicate<>(predicate, type, height)));
            return this;
        }

        private Builder<T> spawnPlacement()
        {
            return spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        }

        private Builder<T> dragonEgg(DragonEggProperties props)
        {
            CommonEvents.CALLBACKS.add(() -> DragonEggProperties.MAP.put(registered.value(), props));
            return this;
        }

        private Holder<EntityType<?>> build(Consumer<EntityType.Builder<T>> consumer)
        {
            EntityType.Builder<T> builder = EntityType.Builder.of(factory, classification);
            consumer.accept(builder);
            return registered = REGISTRY.register(name, () -> builder.build(Wyrmroost.MOD_ID + ":" + name)).getDelegate();
        }

        private static <T extends Entity> Builder<T> creature(String name, EntityType.EntityFactory<T> factory)
        {
            return new Builder<>(name, factory, MobCategory.CREATURE);
        }

        private static <T extends Entity> Builder<T> withClassification(String name, EntityType.EntityFactory<T> factory, MobCategory classification)
        {
            return new Builder<>(name, factory, classification);
        }
    }

    public static class Attributes {
        public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(Registries.ATTRIBUTE, Wyrmroost.MOD_ID);

        public static final Holder<Attribute> PROJECTILE_DAMAGE = ranged("generic.projectileDamage", 2d, 0, 2048d);

        private static Holder<Attribute> ranged(String name, double defaultValue, double min, double max)
        {
            return register(name.toLowerCase().replace('.', '_'), () -> new RangedAttribute("attribute.name." + name, defaultValue, min, max));
        }

        private static Holder<Attribute> register(String name, Supplier<Attribute> attribute)
        {
            return REGISTRY.register(name, attribute);
        }
    }

    public static class Tags {
        public static final TagKey<EntityType<?>> GEODE_ARROWS = tag("geode_arrows");
        public static final TagKey<EntityType<?>> DRAGONS = tag("dragons");
        public static final TagKey<EntityType<?>> SOUL_CRYSTAL_CAN_FIT = tag("soul_crystal_can_fit");
        public static final TagKey<EntityType<?>> HOME_DEFENDER_ATTACKABLE = tag("home_defender_attackable");
        public static final TagKey<EntityType<?>> BUTTERFLY_LEVIATHAN_CONDUIT_TARGETS = tag("butterfly_leviathan_conduit_targets");

        private static TagKey<EntityType<?>> tag(String path) {
            return TagKey.create(Registries.ENTITY_TYPE, Wyrmroost.rl(path));
        }
    }
}
