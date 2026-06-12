package com.github.wolfshotz.wyrmroost.containers;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInvHandler;
import com.github.wolfshotz.wyrmroost.registry.WRIO;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DragonInvContainer extends Container
{
    public static final int MAX_PLAYER_SLOTS = 36;

    public final DragonInvHandler inventory;
    public final Inventory playerInv;

    public DragonInvContainer(DragonInvHandler inv, Inventory playerInv, int windowID)
    {
        super(WRIO.DRAGON_INVENTORY.get(), windowID);
        this.inventory = inv;
        this.playerInv = playerInv;
        inv.dragon.addContainerInfo(this);
    }

    // override method for public exposure
    @Override
    public Slot addSlot(Slot slotIn) { return super.addSlot(slotIn); }

    @Override
    public boolean canInteractWith(Player playerIn) { return inventory.dragon.getOwner() == playerIn; }

    @Override
    public ItemStack transferStackInSlot(Player playerIn, int index)
    {
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack transferring = slot.getStack();
            if ((index < MAX_PLAYER_SLOTS && mergeItemStack(transferring, MAX_PLAYER_SLOTS, inventorySlots.size(), false))
                    || (index >= MAX_PLAYER_SLOTS && mergeItemStack(transferring, 0, MAX_PLAYER_SLOTS, true)))
            {
                if (transferring.isEmpty()) slot.putStack(ItemStack.EMPTY);
                else slot.onSlotChanged();
                return transferring.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public void makeSlots(int firstIndex, int firstX, int firstY, int length, int height, ISlotArea slot)
    {
        for (int y = 0; y < height; ++y)
        {
            for (int x = 0; x < length; ++x)
            {
                if (inventory.getSlots() <= firstIndex)
                {
                    Wyrmroost.LOG.error("TOO MANY SLOTS! ABORTING THE REST! Ended Index: {}, Supposed to be: {}", firstIndex, length * height);
                    return;
                }
                addSlot(slot.get(firstIndex++, firstX + x * 18, firstY + y * 18));
            }
        }
    }

    public void makeSlots(IInventory inventory, int index, int initialX, int initialY, int length, int height)
    {
        for (int y = 0; y < height; ++y)
        {
            for (int x = 0; x < length; ++x)
            {
                if (inventory.getSizeInventory() <= index)
                {
                    Wyrmroost.LOG.error("TOO MANY SLOTS! ABORTING THE REST!");
                    return;
                }
                addSlot(new Slot(inventory, index++, initialX + x * 18, initialY + y * 18));
            }
        }
    }

    public void makePlayerSlots(Inventory playerInv, int initialX, int initialY)
    {
        makeSlots(playerInv, 9, initialX, initialY, 9, 3); // Player inv
        makeSlots(playerInv, 0, initialX, initialY + 58, 9, 1); // Hotbar
    }

    public static INamedContainerProvider getProvider(AbstractDragonEntity dragon)
    {
        return new INamedContainerProvider()
        {
            @Override
            public ITextComponent getDisplayName() { return new StringTextComponent("Dragon Inventory"); }

            @Override
            public Container createMenu(int id, Inventory playersInv, Player player)
            {
                return new DragonInvContainer(dragon.getInvHandler(), playersInv, id);
            }
        };
    }

    public interface ISlotArea
    {
        Slot get(int index, int posX, int posY);
    }
}
