package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.item.UseAction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SilkGlandItem extends Item
{
    private static final int MAX_USE_TIME = 72000;

    public SilkGlandItem()
    {
        super(WRItems.builder().maxStackSize(1));
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.BOW;
    }

    @Override
    public InteractionResult<ItemStack> onItemRightClick(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft)
    {
        if (!(entity instanceof Player)) return;


        int useTime = MAX_USE_TIME - timeLeft;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return MAX_USE_TIME;
    }
}
