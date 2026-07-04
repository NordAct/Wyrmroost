package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class TrumpetItem extends Item
{
    public TrumpetItem() { super(WRItems.builder()); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        RandomSource random = player.getRandom();
        SoundEvent sound = random.nextBoolean()? WRSounds.ENTITY_BFLY_IDLE.value() : WRSounds.ENTITY_BFLY_ROAR.value();
        world.playSound(player, player.blockPosition(), sound, SoundSource.PLAYERS, 0.75f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        player.getCooldowns().addCooldown(this, 50);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.translatable("item.wyrmroost.trumpet.desc").withStyle(ChatFormatting.GRAY));
    }
}
