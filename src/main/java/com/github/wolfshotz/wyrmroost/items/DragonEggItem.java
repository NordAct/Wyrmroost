package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.render.DragonEggStackRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggEntity;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class DragonEggItem extends Item
{
    public DragonEggItem()
    {
        super(WRItems.builder().maxStackSize(1).setISTER(() -> DragonEggStackRenderer::new));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
    {
        if (!player.isCreative()) return false;
        if (!entity.isAlive()) return false;
        if (!(entity instanceof AbstractDragonEntity)) return false;

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(DragonEggEntity.DATA_DRAGON_TYPE, EntityType.getKey(entity.getType()).toString());
        nbt.putInt(DragonEggEntity.DATA_HATCH_TIME, DragonEggProperties.MAP.get(entity.getType()).getHatchTime());
        stack.setTag(nbt);

        player.sendStatusMessage(getDisplayName(stack), true);
        return true;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx)
    {
        Player player = ctx.getPlayer();
        if (player.isShiftKeyDown()) return super.onItemUse(ctx);

        Level world = ctx.getWorld();
        CompoundNBT tag = ctx.getItem().getTag();
        BlockPos pos = ctx.getPos();
        BlockState state = world.getBlockState(pos);

        if (tag == null || !tag.contains(DragonEggEntity.DATA_DRAGON_TYPE)) return ActionResultType.PASS;
        if (!state.getCollisionShape(world, pos).isEmpty()) pos = pos.relative(ctx.getFace());
        if (!world.getEntitiesWithinAABB(DragonEggEntity.class, new AxisAlignedBB(pos)).isEmpty())
            return ActionResultType.FAIL;

        DragonEggEntity eggEntity = new DragonEggEntity(ModUtils.getEntityTypeByKey(tag.getString(DragonEggEntity.DATA_DRAGON_TYPE)), tag.getInt(DragonEggEntity.DATA_HATCH_TIME), world);
        eggEntity.setPos(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);

        if (!world.isRemote) world.addEntity(eggEntity);
        if (!player.isCreative()) player.setHeldItem(ctx.getHand(), ItemStack.EMPTY);
        
        return ActionResultType.SUCCESS;
    }
    
    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return super.getDisplayName(stack);
        Optional<EntityType<?>> type = EntityType.byKey(tag.getString(DragonEggEntity.DATA_DRAGON_TYPE));
        
        if (type.isPresent())
        {
            String dragonTranslation = type.get().getDescription().getString();
            return new TranslationTextComponent(dragonTranslation + " ").append(new TranslationTextComponent(getDescriptionId()));
        }
        
        return super.getDisplayName(stack);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable Level worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        CompoundNBT tag = stack.getTag();

        if (tag != null && tag.contains(DragonEggEntity.DATA_HATCH_TIME))
            tooltip.add(new TranslationTextComponent("item.wyrmroost.egg.tooltip", tag.getInt(DragonEggEntity.DATA_HATCH_TIME) / 1200).mergeStyle(TextFormatting.AQUA));
        Player player = ClientEvents.getPlayer();
        if (player != null && player.isCreative())
            tooltip.add(new TranslationTextComponent("item.wyrmroost.egg.creativetooltip").mergeStyle(TextFormatting.GRAY));
    }

    public static ItemStack getStack(EntityType<?> type)
    {
        return getStack(type, DragonEggProperties.MAP.get(type).getHatchTime());
    }

    public static ItemStack getStack(EntityType<?> type, int hatchTime)
    {
        ItemStack stack = new ItemStack(WRItems.DRAGON_EGG.get());
        CompoundNBT tag = new CompoundNBT();
        tag.putString(DragonEggEntity.DATA_DRAGON_TYPE, EntityType.getKey(type).toString());
        tag.putInt(DragonEggEntity.DATA_HATCH_TIME, hatchTime);
        stack.setTag(tag);
        return stack;
    }
}
