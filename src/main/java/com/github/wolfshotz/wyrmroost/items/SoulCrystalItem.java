package com.github.wolfshotz.wyrmroost.items;

import ActionResultType;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.EntitySize;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SoulCrystalItem extends Item
{
    public static final String DATA_DRAGON = "DragonData";

    public SoulCrystalItem()
    {
        super(WRItems.builder().maxStackSize(1));
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand)
    {
        Level world = player.level;
        if (containsDragon(stack)) return ActionResultType.PASS;
        if (!isSuitableEntity(target)) return ActionResultType.PASS;
        TamableAnimal dragon = (TamableAnimal) target;
        if (dragon.getOwner() != player)
        {
            player.sendStatusMessage(new TranslationTextComponent("item.wyrmroost.soul_crystal.not_owner").mergeStyle(TextFormatting.RED), true);
            return ActionResultType.FAIL;
        }

        if (!dragon.getPassengers().isEmpty()) dragon.removePassengers();
        if (!world.isRemote)
        {
            CompoundNBT tag = stack.getOrCreateTag();
            CompoundNBT dragonTag = dragon.serializeNBT();
            dragonTag.putString("OwnerName", player.getName().getUnformattedComponentText());
            tag.put(DATA_DRAGON, dragonTag); // Serializing the dragons data, including its id.
            stack.setTag(tag);
            dragon.remove();
            player.setHeldItem(hand, stack);
            world.playSound(null, player.getPosition(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.AMBIENT, 1, 1);
        }
        else // Client side Aesthetics
        {
            double width = dragon.getBbWidth();
            for (int i = 0; i <= Math.floor(width) * 25; ++i)
            {
                double calcX = Mth.cos(i + 360 / Mafs.PI * 360f) * (width * 1.5);
                double calcZ = Mth.sin(i + 360 / Mafs.PI * 360f) * (width * 1.5);
                double x = dragon.getPosX() + calcX;
                double y = dragon.getPosY() + (dragon.getBbHeight() * 1.8);
                double z = dragon.getPosZ() + calcZ;
                double xMot = -calcX / 5f;
                double yMot = -(dragon.getBbHeight() / 8);
                double zMot = -calcZ / 5f;

                world.addParticle(ParticleTypes.END_ROD, x, y, z, xMot, yMot, zMot);
            }
        }
        return ActionResultType.func_233537_a_(world.isRemote);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        ItemStack stack = context.getItem();
        if (!containsDragon(stack)) return ActionResultType.PASS;
        Level world = context.getWorld();
        Player player = context.getPlayer();
        if (!stack.getChildTag(DATA_DRAGON).getUniqueId("Owner").equals(player.getUniqueID()))
        {
            player.sendStatusMessage(new TranslationTextComponent("item.wyrmroost.soul_crystal.not_owner").mergeStyle(TextFormatting.RED), true);
            return ActionResultType.FAIL;
        }
        TamableAnimal dragon = getContained(stack, world);
        BlockPos pos = context.getPos().offset(context.getFace());

        dragon.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, dragon.rotationYaw, dragon.rotationPitch); // update the position now for collision checking
        if (!world.hasNoCollisions(dragon, dragon.getBoundingBox()))
        {
            player.sendStatusMessage(new TranslationTextComponent("item.wyrmroost.soul_crystal.fail").mergeStyle(TextFormatting.RED), true);
            return ActionResultType.FAIL;
        }

        if (!world.isRemote) // Spawn the entity on the server side only
        {
            stack.removeChildTag(DATA_DRAGON);
            world.addEntity(dragon);
            world.playSound(null, dragon.getPosition(), SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.AMBIENT, 1, 1);
        }
        else // Client Side Aesthetics
        {
            EntitySize size = dragon.getSize(dragon.getPose());

            double posX = pos.getX() + 0.5d;
            double posY = pos.getY() + (size.height / 2);
            double posZ = pos.getZ() + 0.5d;
            for (int i = 0; i < dragon.getBbWidth() * 25; ++i)
            {
                double x = Mth.cos(i + 360 / Mafs.PI * 360f) * (dragon.getBbWidth() * 1.5d);
                double z = Mth.sin(i + 360 / Mafs.PI * 360f) * (dragon.getBbWidth() * 1.5d);
                double xMot = x / 10f;
                double yMot = dragon.getBbHeight() / 18f;
                double zMot = z / 10f;

                world.addParticle(ParticleTypes.END_ROD, posX, posY, posZ, xMot, yMot, zMot);
                world.addParticle(ParticleTypes.CLOUD, posX, posY + (i * 0.25), posZ, 0, 0, 0);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable Level world, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if (containsDragon(stack))
        {
            CompoundNBT tag = stack.getTag().getCompound(DATA_DRAGON);
            ITextComponent name;

            if (tag.contains("CustomName"))
                name = ITextComponent.Serializer.func_240643_a_(tag.getString("CustomName"));
            else name = EntityType.byKey(tag.getString("id")).orElse(null).getName();

            tooltip.add(name.copyRaw().mergeStyle(TextFormatting.BOLD));
            tooltip.add(new StringTextComponent("Tamed by ").append(new StringTextComponent(tag.getString("OwnerName")).mergeStyle(TextFormatting.ITALIC)));
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        TranslationTextComponent name = (TranslationTextComponent) super.getDisplayName(stack);
        if (containsDragon(stack)) name.mergeStyle(TextFormatting.AQUA).mergeStyle(TextFormatting.ITALIC);
        return name;
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return containsDragon(stack);
    }

    private static boolean containsDragon(ItemStack stack)
    {
        return stack.hasTag() && stack.getTag().contains(DATA_DRAGON);
    }

    @Nullable
    private static TamableAnimal getContained(ItemStack stack, Level world)
    {
        if (!containsDragon(stack)) return null;
        CompoundNBT tag = stack.getTag().getCompound(DATA_DRAGON);
        EntityType<?> type = EntityType.byKey(tag.getString("id")).orElse(null);
        if (type == null) return null;
        TamableAnimal dragon = (TamableAnimal) type.create(world);
        dragon.deserializeNBT(tag);
        return dragon;
    }

    private static boolean isSuitableEntity(LivingEntity entity)
    {
        if (entity instanceof TamableAnimal)
        {
            if (entity instanceof AbstractDragonEntity) return true;
            ResourceLocation rl = entity.getType().getRegistryName();
            switch (rl.getNamespace())
            {
                case "dragonmounts":
                case "wings":
                    return true;
                case "iceandfire":
                    String path = rl.getPath();
                    return path.contains("dragon") || path.contains("amphithere");
                default:
                    break;
            }
        }
        return false;
    }
}
