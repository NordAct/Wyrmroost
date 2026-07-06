package com.github.wolfshotz.wyrmroost;

import com.github.wolfshotz.wyrmroost.client.screen.DebugScreen;
import com.github.wolfshotz.wyrmroost.data.DataGatherer;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.items.CoinDragonItem;
import com.github.wolfshotz.wyrmroost.items.base.ArmorBase;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Reflection is shit and we shouldn't use it
 * - Some communist coding wyrmroost 2020
 * <p>
 * Manually add listeners
 */
public class CommonEvents
{
    public static final List<Runnable> CALLBACKS = new ArrayList<>();

    public static void load(IEventBus bus) {
        bus.addListener(CommonEvents::commonSetup);
        bus.addListener(WRConfig::configLoad);
        bus.addListener(DataGatherer::gather);
        bus.addListener(CommonEvents::entityAttributes);
        bus.addListener(Wyrmroost::registerPayloads);
        bus.addListener(CommonEvents::spawnPlacements);
    }

    // ====================
    //       Mod Bus
    // ====================

    public static void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            CALLBACKS.forEach(Runnable::run);
            CALLBACKS.clear();
        });
        //WRWorld.Features.init();
    }

    // =====================
    //      Forge Bus
    // =====================

    @SubscribeEvent
    public static void debugStick(PlayerInteractEvent.EntityInteract evt)
    {
        if (!WRConfig.debugMode) return;
        Player player = evt.getEntity();
        ItemStack stack = player.getItemInHand(evt.getHand());
        if (stack.getItem() != Items.STICK || !stack.getDisplayName().getString().equals("Debug Stick"))
            return;

        evt.setCanceled(true);
        evt.setCancellationResult(InteractionResult.SUCCESS);

        Entity entity = evt.getTarget();
        entity.refreshDimensions();

        if (!(entity instanceof AbstractDragonEntity)) return;
        AbstractDragonEntity dragon = (AbstractDragonEntity) entity;

        if (player.isShiftKeyDown()) dragon.tame(true, player);
        else
        {
            if (dragon.level().isClientSide()) DebugScreen.open(dragon);
            else Wyrmroost.LOG.info(dragon.getNavigation().getPath() == null? "null" : dragon.getNavigation().getPath().getTarget().toString());
        }
    }

    @SubscribeEvent
    public static void onChangeEquipment(LivingEquipmentChangeEvent evt)
    {
        ArmorBase initial;
        if (evt.getTo().getItem() instanceof ArmorBase) initial = (ArmorBase) evt.getTo().getItem();
        else if (evt.getFrom().getItem() instanceof ArmorBase) initial = (ArmorBase) evt.getFrom().getItem();
        else return;

        LivingEntity entity = evt.getEntity();
        initial.applyFullSetBonus(entity, ArmorBase.hasFullSet(entity));
    }

    @SubscribeEvent
    public static void loadLoot(LootTableLoadEvent evt)
    {
        if (evt.getName().equals(BuiltInLootTables.ABANDONED_MINESHAFT))
            evt.getTable().addPool(LootPool.lootPool()
                    .name("coin_dragon_inject")
                    .add(CoinDragonItem.getLootEntry())
                    .build());
    }

    public static void entityAttributes(EntityAttributeCreationEvent event) {
        WREntities.ATTRIBUTES.forEach(pair -> event.put((EntityType<? extends LivingEntity>) EntityType.byString(pair.getFirst().toString()).orElseThrow(), pair.getSecond().get().build()));
    }

    public static void spawnPlacements (RegisterSpawnPlacementsEvent event) {
        WREntities.SPAWN_PREDICATES.forEach(pair -> event.register(EntityType.byString(pair.getFirst().toString()).orElseThrow(), pair.getSecond().getSpawnType(), pair.getSecond().getHeightmapType(), pair.getSecond().build(), RegisterSpawnPlacementsEvent.Operation.OR));
    }
}
