package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class TrumpetItem extends Item
{
    public TrumpetItem() { super(WRItems.builder()); }

    @Override
    public InteractionResult<ItemStack> onItemRightClick(Level world, Player player, InteractionHand hand)
    {
        SoundEvent sound = player.getRNG().nextBoolean()? WRSounds.ENTITY_BFLY_IDLE.get() : WRSounds.ENTITY_BFLY_ROAR.get();
        world.playSound(player, player.getPosition(), sound, SoundCategory.PLAYERS, 0.75f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        player.getCooldownTracker().setCooldown(this, 50);
        return InteractionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable Level worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(new TranslationTextComponent("item.wyrmroost.trumpet.desc").mergeStyle(TextFormatting.GRAY));
    }
}
