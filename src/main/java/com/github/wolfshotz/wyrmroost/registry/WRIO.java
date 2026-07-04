package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WRIO
{
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, Wyrmroost.MOD_ID);

    public static final Holder<MenuType<?>> DRAGON_INVENTORY = register("dragon_inventory", () -> getDragonInvContainer());


    public static <T extends AbstractContainerMenu> Holder<MenuType<?>> register(String name, Supplier<MenuType<T>> type)
    {
        return REGISTRY.register(name, type).getDelegate();
    }

    private static MenuType<DragonInvContainer> getDragonInvContainer()
    {
        return IMenuTypeExtension.create(((windowId, inv, data) ->
        {
            AbstractDragonEntity dragon = (AbstractDragonEntity) ClientEvents.getLevel().getEntity(data.readInt());
            return new DragonInvContainer(dragon.getInvHandler(), inv, windowId);
        }));
    }
}
