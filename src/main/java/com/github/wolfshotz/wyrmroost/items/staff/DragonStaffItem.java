package com.github.wolfshotz.wyrmroost.items.staff;

import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WRDataComponentTypes;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class DragonStaffItem extends Item
{
    public static final String DATA_DRAGON_ID = "BoundDragon"; // int
    public static final String DATA_ACTION = "Action";

    public DragonStaffItem() {
        super(WRItems.builder().stacksTo(1));
    }


    /**
     * Triggered when right clicked on air
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && stack.getComponents().has(WRDataComponentTypes.DRAGON_ID_COMPONENT.get()))
        {
            reset(stack);
            ModUtils.playLocalSound(world, player.blockPosition(), SoundEvents.SCAFFOLDING_STEP, 1, 1);
            return InteractionResultHolder.success(stack);
        }

        AbstractDragonEntity dragon = getBoundDragon(world, stack);
        if (dragon != null && getAction(stack).rightClick(dragon, player, stack))
            return InteractionResultHolder.success(stack);
        return InteractionResultHolder.pass(stack);
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
                ModUtils.playLocalSound(player.level(), player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, 1, 1);
                return true;
            }
        }
        return false;
    }

    /**
     * Triggered when Right clicking an entity
     */
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        if (target instanceof AbstractDragonEntity)
        {
            AbstractDragonEntity dragon = (AbstractDragonEntity) target;
            if (dragon.isOwnedBy(playerIn))
            {
                bindDragon(dragon, stack);
                if (playerIn.level().isClientSide()) StaffScreen.open(dragon, stack);
                return InteractionResult.sidedSuccess(playerIn.level().isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Triggered when right clicked on a block
     */

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer().isCrouching()) return InteractionResult.PASS;
        ItemStack stack = context.getItemInHand();
        AbstractDragonEntity dragon = getBoundDragon(context.getLevel(), stack);
        if (dragon != null && getAction(stack).clickBlock(dragon, context)) return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.translatable("item.wyrmroost.dragon_staff.desc").withStyle(ChatFormatting.GRAY));
        AbstractDragonEntity dragon = getBoundDragon(context.level(), stack);
        if (dragon != null)
        {
            tooltip.add(Component.translatable("item.wyrmroost.dragon_staff.bound", dragon.getName()).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("item.wyrmroost.dragon_staff.action", getAction(stack).getTranslation(dragon)).withStyle(ChatFormatting.AQUA));
        }
    }

    /**
     * Not ideal whatsoever, but its literally the best we can do.
     * Storing the uuid of the dragons means we would have to get that dragon by uuid, which we cant on client,
     * and this item requires almost constant syncing.
     * The solution is to just remove everything.
     */
    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        reset(stack);
    }

    @Override
    public boolean useOnRelease(ItemStack stack) { return getAction(stack) != StaffAction.DEFAULT; }

    public static void reset(ItemStack stack)
    {
        if (stack.getComponents().has(WRDataComponentTypes.DRAGON_ID_COMPONENT.get())) stack.remove(WRDataComponentTypes.DRAGON_ID_COMPONENT.get());
        if (stack.getComponents().has(WRDataComponentTypes.ACTION_COMPONENT.get())) stack.remove(WRDataComponentTypes.ACTION_COMPONENT.get());
    }

    public static void setAction(StaffAction action, Player player, ItemStack stack)
    {
        assertStaff(stack);
        stack.set(WRDataComponentTypes.ACTION_COMPONENT, action.ordinal());
        getAction(stack).onSelected(getBoundDragon(player.level(), stack), player, stack);
    }

    public static StaffAction getAction(ItemStack stack)
    {
        assertStaff(stack);
        if (stack.getComponents().has(WRDataComponentTypes.ACTION_COMPONENT.get()))
            return StaffAction.VALUES[stack.getComponents().get(WRDataComponentTypes.ACTION_COMPONENT.get())];
        return StaffAction.DEFAULT;
    }

    public static void bindDragon(AbstractDragonEntity dragon, ItemStack stack)
    {
        assertStaff(stack);

        stack.set(WRDataComponentTypes.DRAGON_ID_COMPONENT.get(), dragon.getId());
    }

    @Nullable
    public static AbstractDragonEntity getBoundDragon(Level world, ItemStack stack)
    {
        assertStaff(stack);
        if (stack.getComponents().has(WRDataComponentTypes.DRAGON_ID_COMPONENT.get())) {
            Entity entity = world.getEntity(stack.getComponents().get(WRDataComponentTypes.DRAGON_ID_COMPONENT.get()));
            if (entity instanceof AbstractDragonEntity) return ((AbstractDragonEntity) entity);
        }
        return null;
    }

    public static void assertStaff(ItemStack stack)
    {
        if (!(stack.getItem() instanceof DragonStaffItem))
            throw new AssertionError(String.format("This isnt a staff wtf? [%s] Please contact the mod author", stack.getItem().builtInRegistryHolder().getKey().location()));
    }
}
