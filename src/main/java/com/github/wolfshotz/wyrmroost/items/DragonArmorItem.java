package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.registry.WRItems;
import java.util.UUID;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class DragonArmorItem extends Item
{
    public static final UUID ARMOR_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    private final int dmgReduction, enchantability;

    public DragonArmorItem(int dmgReduction, int enchantability)
    {
        super(WRItems.builder().maxStackSize(1));
        this.dmgReduction = dmgReduction;
        this.enchantability = enchantability;
    }

    @Override
    public int getItemEnchantability() { return enchantability; }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment == Enchantments.PROTECTION;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) { return true; }

    public double getDmgReduction() { return dmgReduction; }

    public static double getDmgReduction(ItemStack stack)
    {
        Item item = stack.getItem();
        if (!(item instanceof DragonArmorItem))
            throw new AssertionError("uhh this isn't a an armor: " + item.getRegistryName().toString());

        return ((DragonArmorItem) item).getDmgReduction() + EnchantmentHelper.getEnchantmentsForCrafting(stack).getOrDefault(Enchantments.PROTECTION, 0);
    }
}
