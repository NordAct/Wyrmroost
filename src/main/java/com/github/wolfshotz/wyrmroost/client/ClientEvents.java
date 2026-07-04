package com.github.wolfshotz.wyrmroost.client;

import com.github.wolfshotz.wyrmroost.client.render.RenderHelper;
import com.github.wolfshotz.wyrmroost.client.render.entity.projectile.BreathWeaponRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.items.LazySpawnEggItem;
import com.github.wolfshotz.wyrmroost.util.animation.IAnimatable;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMaterialAtlasesEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * EventBus listeners on CLIENT distribution
 * Also a client helper class because yes.
 */
@SuppressWarnings("unused")
public class ClientEvents
{
    public static final List<Runnable> CALLBACKS = new ArrayList<>();

    public static void load(IEventBus bus) {

        bus.addListener(ClientEvents::clientSetup);
        bus.addListener(ClientEvents::addAtlas);
        bus.addListener(ClientEvents::itemColors);

        bus.addListener(RenderHelper::renderWorld);
        bus.addListener(RenderHelper::renderEntities);
//        forgeBus.addListener(RenderHelper::renderFog);
//        forgeBus.addListener(RenderHelper::fogColors);
        bus.addListener(ClientEvents::cameraPerspective);
    }

    // ====================
    //       Mod Bus
    // ====================

    public static void clientSetup(final FMLClientSetupEvent event)
    {
        CALLBACKS.forEach(Runnable::run);
        CALLBACKS.clear();
    }

    public static void addAtlas(RegisterMaterialAtlasesEvent evt) {
        evt.register(BreathWeaponRenderer.BLUE_FIRE_MATERIAL.atlasLocation(), BreathWeaponRenderer.BLUE_FIRE_MATERIAL.texture());
    }

    public static void itemColors(RegisterColorHandlersEvent.Item evt)
    {
        ItemColors handler = evt.getItemColors();
        ItemColor func = (stack, tintIndex) -> ((LazySpawnEggItem) stack.getItem()).getColor(tintIndex);
        for (LazySpawnEggItem e : LazySpawnEggItem.SPAWN_EGGS) handler.register(func, e);
    }

    // =====================
    //      Forge Bus
    // =====================

    public static void cameraPerspective(CalculateDetachedCameraDistanceEvent event)
    {
        Minecraft mc = getClient();
        Entity entity = mc.player.getVehicle();
        if (!(entity instanceof AbstractDragonEntity)) return;
        CameraType view = mc.options.getCameraType();

        if (view != CameraType.FIRST_PERSON)
            ((AbstractDragonEntity) entity).setMountCameraAngles(view == CameraType.THIRD_PERSON_BACK, event);
    }

    // =====================

    // for class loading issues
    public static Minecraft getClient()
    {
        return Minecraft.getInstance();
    }

    public static ClientLevel getLevel()
    {
        return getClient().level;
    }

    public static Player getPlayer()
    {
        return getClient().player;
    }

    public static Vec3 getProjectedView()
    {
        return getClient().gameRenderer.getMainCamera().getPosition();
    }

    public static float getPartialTicks()
    {
        return getClient().getTimer().getGameTimeDeltaPartialTick(false);
    }

    public static boolean handleAnimationPacket(int entityID, int animationIndex)
    {
        Level world = ClientEvents.getLevel();
        IAnimatable entity = (IAnimatable) world.getEntity(entityID);

        if (animationIndex < 0) entity.setAnimation(IAnimatable.NO_ANIMATION);
        else entity.setAnimation(entity.getAnimations()[animationIndex]);
        return true;
    }
}
