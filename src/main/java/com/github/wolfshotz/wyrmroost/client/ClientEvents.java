package com.github.wolfshotz.wyrmroost.client;

import com.github.wolfshotz.wyrmroost.client.render.DragonEggStackRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.projectile.BreathWeaponRenderer;
import com.github.wolfshotz.wyrmroost.client.screen.DragonInvScreen;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRIO;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.animation.IAnimatable;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * EventBus listeners on CLIENT distribution
 * Also a client helper class because yes.
 */
@SuppressWarnings("unused")
@EventBusSubscriber
public class ClientEvents
{
    public static final List<Runnable> CALLBACKS = new ArrayList<>();

    public static void load(IEventBus bus) {

        bus.addListener(ClientEvents::clientSetup);
        bus.addListener(ClientEvents::addAtlas);
        bus.addListener(ClientEvents::registerEntityRenderers);
        bus.addListener(ClientEvents::registerMenuScreens);
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

    // =====================
    //      Forge Bus
    // =====================

    @SubscribeEvent
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

        if (world.getEntity(entityID) instanceof IAnimatable entity) {
            if (animationIndex < 0) entity.setAnimation(IAnimatable.NO_ANIMATION);
            else entity.setAnimation(entity.getAnimations()[animationIndex]);
        }
        return true;
    }

     // on the mod event bus only on the physical client
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        WREntities.RENDERERS.forEach((pair) -> {
            event.registerEntityRenderer(EntityType.byString(pair.getFirst().toString()).orElseThrow(),
                    // Pass the context to an empty (default) constructor call
                    context -> pair.getSecond().apply(context)
            );
        });
    }

    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(WRIO.DRAGON_INVENTORY.value(), DragonInvScreen::new);
    }

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(
                // The only instance of our IClientItemExtensions, and as such, the only instance of our BEWLR.
                new IClientItemExtensions() {
                    private final DragonEggStackRenderer renderer = new DragonEggStackRenderer();
                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return renderer;
                    }
                },
                // A vararg list of items that use this BEWLR.
                WRItems.DRAGON_EGG.value()
        );
    }
}
