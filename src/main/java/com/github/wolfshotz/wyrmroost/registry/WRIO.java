package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WRIO
{
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, Wyrmroost.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<DragonInvContainer>> DRAGON_INVENTORY = REGISTRY.register("dragon_inventory", () -> getDragonInvContainer());

    private static MenuType<DragonInvContainer> getDragonInvContainer()
    {
        return IMenuTypeExtension.create(((windowId, inv, data) ->
        {
            AbstractDragonEntity dragon = (AbstractDragonEntity) ClientEvents.getLevel().getEntity(data.readInt());
            return new DragonInvContainer(dragon.getInvHandler(), inv, windowId);
        }));
    }
}
