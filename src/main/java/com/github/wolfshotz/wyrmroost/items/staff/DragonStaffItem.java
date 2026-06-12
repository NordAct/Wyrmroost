package com.github.wolfshotz.wyrmroost.items.staff;

import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class DragonStaffItem extends Item
{
    public static final String DATA_DRAGON_ID = "BoundDragon"; // int
    public static final String DATA_ACTION = "Action";

    public DragonStaffItem() { super(WRItems.builder().maxStackSize(1)); }

    /**
     * Triggered when right clicked on air
     */
    @Override
    public InteractionResult<ItemStack> onItemRightClick(Level world, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isShiftKeyDown() && stack.hasTag() && stack.getTag().contains(DATA_DRAGON_ID))
        {
            reset(stack.getTag());
            ModUtils.playLocalSound(world, player.getPosition(), SoundEvents.BLOCK_SCAFFOLDING_STEP, 1, 1);
            return InteractionResult.resultSuccess(stack);
        }

        AbstractDragonEntity dragon = getBoundDragon(world, stack);
        if (dragon != null && getAction(stack).rightClick(dragon, player, stack))
            return InteractionResult.resultSuccess(stack);
        return InteractionResult.resultPass(stack);
    }

    /**
     * Triggered when Attacking an entity with this item
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
    {
        if (entity instanceof AbstractDragonEntity)
        {
            AbstractDragonEntity dragon = (AbstractDragonEntity) entity;
            if (dragon.isOwnedBy(player))
            {
                bindDragon(dragon, stack);
                ModUtils.playLocalSound(player.level, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                return true;
            }
        }
        return false;
    }

    /**
     * Triggered when Right clicking an entity
     */
    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand)
    {
        if (target instanceof AbstractDragonEntity)
        {
            AbstractDragonEntity dragon = (AbstractDragonEntity) target;
            if (dragon.isOwnedBy(playerIn))
            {
                bindDragon(dragon, stack);
                if (playerIn.world.isRemote) StaffScreen.open(dragon, stack);
                return ActionResultType.func_233537_a_(playerIn.world.isRemote);
            }
        }
        return ActionResultType.PASS;
    }

    /**
     * Triggered when right clicked on a block
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        if (context.getPlayer().isSneaking()) return ActionResultType.PASS;
        ItemStack stack = context.getItem();
        AbstractDragonEntity dragon = getBoundDragon(context.getWorld(), stack);
        if (dragon != null && getAction(stack).clickBlock(dragon, context)) return ActionResultType.SUCCESS;
        return ActionResultType.PASS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable Level worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(new TranslationTextComponent("item.wyrmroost.dragon_staff.desc").mergeStyle(TextFormatting.GRAY));
        if (stack.hasTag())
        {
            AbstractDragonEntity dragon = getBoundDragon(worldIn, stack);
            if (dragon != null)
            {
                tooltip.add(new TranslationTextComponent("item.wyrmroost.dragon_staff.bound", ((IFormattableTextComponent) dragon.getName()).mergeStyle(TextFormatting.AQUA)));
                tooltip.add(new TranslationTextComponent("item.wyrmroost.dragon_staff.action", getAction(stack).getTranslation(dragon)).mergeStyle(TextFormatting.AQUA));
            }
        }
    }

    /**
     * Not ideal whatsoever, but its literally the best we can do.
     * Storing the uuid of the dragons means we would have to get that dragon by uuid, which we cant on client,
     * and this item requires almost constant syncing.
     * The solution is to just remove everything.
     */
    @Override
    public boolean updateItemStackNBT(CompoundNBT nbt)
    {
        reset(nbt);
        return false;
    }

    @Override
    public boolean hasEffect(ItemStack stack) { return getAction(stack) != StaffAction.DEFAULT; }

    public static void reset(@Nullable CompoundNBT tag)
    {
        if (tag == null) return;
        if (tag.contains(DATA_DRAGON_ID)) tag.remove(DATA_DRAGON_ID);
        if (tag.contains(DATA_ACTION)) tag.remove(DATA_ACTION);
    }

    public static void setAction(StaffAction action, Player player, ItemStack stack)
    {
        assertStaff(stack);
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putInt(DATA_ACTION, action.ordinal());
        getAction(stack).onSelected(getBoundDragon(player.level, stack), player, stack);
    }

    public static StaffAction getAction(ItemStack stack)
    {
        assertStaff(stack);
        if (stack.hasTag())
        {
            CompoundNBT tag = stack.getTag();
            if (tag.contains(DATA_ACTION)) return StaffAction.VALUES[tag.getInt(DATA_ACTION)];
        }
        return StaffAction.DEFAULT;
    }

    public static void bindDragon(AbstractDragonEntity dragon, ItemStack stack)
    {
        assertStaff(stack);
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(DATA_DRAGON_ID, dragon.getEntityId());
    }

    @Nullable
    public static AbstractDragonEntity getBoundDragon(Level world, ItemStack stack)
    {
        assertStaff(stack);
        if (stack.hasTag())
        {
            CompoundNBT tag = stack.getTag();
            if (tag.contains(DATA_DRAGON_ID))
            {
                Entity entity = world.getEntityByID(tag.getInt(DATA_DRAGON_ID));
                if (entity instanceof AbstractDragonEntity) return ((AbstractDragonEntity) entity);
            }
        }
        return null;
    }

    public static void assertStaff(ItemStack stack)
    {
        if (!(stack.getItem() instanceof DragonStaffItem))
            throw new AssertionError(String.format("This isnt a staff wtf? [%s] Please contact the mod author", stack.getItem().getRegistryName()));
    }
}
