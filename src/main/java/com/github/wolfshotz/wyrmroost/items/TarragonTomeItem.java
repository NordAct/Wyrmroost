//package com.github.wolfshotz.wyrmroost.items;
//
//import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
//import com.github.wolfshotz.wyrmroost.registry.WRItems;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.ActionResultType;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.StringTextComponent;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraft.util.text.TranslationTextComponent;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import javax.annotation.Nullable;
//import java.util.List;
//
//public class TarragonTomeItem extends Item
//{
//    public TarragonTomeItem()
//    {
//        super(WRItems.builder().maxStackSize(1));
//    }
//
//    @Override
//    public InteractionResult<ItemStack> onItemRightClick(Level world, Player player, InteractionHand hand)
//    {
//        return new InteractionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
//    }
//
//    @Override
//    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
//    {
//        if (!(entity instanceof AbstractDragonEntity)) return false;
//        if (stack.getTag() == null) stack.setTag(new CompoundNBT());
//        CompoundNBT tag = stack.getTag();
//        tag.putByte(entity.getType().getRegistryName().getPath(), (byte) 0);
//        return true;
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable Level worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
//    {
//        tooltip.add(new TranslationTextComponent(this.getDescriptionId() + ".tooltip")
//                            .append(new StringTextComponent("sgdshdf")
//                                                   .mergeStyle(TextFormatting.OBFUSCATED))
//                            .mergeStyle(TextFormatting.GRAY));
//    }
//}
