package com.github.wolfshotz.wyrmroost;

import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.network.packets.*;
import com.github.wolfshotz.wyrmroost.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Wyrmroost.MOD_ID)
public class Wyrmroost
{
    public static final String MOD_ID = "wyrmroost";
    public static final Logger LOG = LogManager.getLogger(MOD_ID);

    public Wyrmroost(ModContainer container, IEventBus bus) {
        WRItems.REGISTRY.register(bus);
        WRBlocks.REGISTRY.register(bus);
        WRIO.REGISTRY.register(bus);
        WRSounds.REGISTRY.register(bus);
        WRCreativeModeTab.REGISTRY.register(bus);
        WRArmorMaterials.REGISTRY.register(bus);
        WREntities.Attributes.REGISTRY.register(bus);
        WREntities.REGISTRY.register(bus);
        WRDataComponentTypes.REGISTRY.register(bus);

        CommonEvents.load(bus);
        if (FMLLoader.getDist() == Dist.CLIENT) ClientEvents.load(bus);

        container.registerConfig(ModConfig.Type.COMMON, WRConfig.Common.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, WRConfig.Client.SPEC);
        container.registerConfig(ModConfig.Type.SERVER, WRConfig.Server.SPEC);
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0").executesOn(HandlerThread.MAIN);
        registrar.playToClient(AddPassengerPacket.TYPE, AddPassengerPacket.STREAM_CODEC, AddPassengerPacket::handle);
        registrar.playToClient(AnimationPacket.TYPE, AnimationPacket.STREAM_CODEC, AnimationPacket::handle);
        registrar.playToServer(KeybindPacket.TYPE, KeybindPacket.STREAM_CODEC, KeybindPacket::handle);
        registrar.playToServer(RenameEntityPacket.TYPE, RenameEntityPacket.STREAM_CODEC, RenameEntityPacket::handle);
        registrar.playToServer(SGGlidePacket.TYPE, SGGlidePacket.STREAM_CODEC, SGGlidePacket::handle);
        registrar.playToServer(StaffActionPacket.TYPE, StaffActionPacket.STREAM_CODEC, StaffActionPacket::handle);
    }

    /**
     * Register a new Wyrmroost Specific Resource Location. <P>
     * Don't bash me for the method name it makes total sense ffs: <P>
     * <b><i>r</i></b>esource <P>
     * <b><i>l</i></b>ocation <P>
     *
     * @return somethin related to a resource idk
     */
    public static ResourceLocation rl(String path) { return ResourceLocation.fromNamespaceAndPath(MOD_ID, path); }
}
