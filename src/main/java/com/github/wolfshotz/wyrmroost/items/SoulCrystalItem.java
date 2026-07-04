package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WRDataComponentTypes;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SoulCrystalItem extends Item {
    public SoulCrystalItem()
    {
        super(WRItems.builder().stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level world = player.level();
        if (containsDragon(stack)) return InteractionResult.PASS;
        if (!isSuitableEntity(target)) return InteractionResult.PASS;
        TamableAnimal dragon = (TamableAnimal) target;
        if (dragon.getOwner() != player) {
            player.sendSystemMessage(Component.translatable("item.wyrmroost.soul_crystal.not_owner").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        if (!dragon.getPassengers().isEmpty()) dragon.ejectPassengers();
        if (!world.isClientSide()) {
            CompoundTag dragonTag = new CompoundTag();
            dragon.save(dragonTag);
            dragonTag.putString("OwnerName", player.getName().getString());
            stack.set(WRDataComponentTypes.DRAGON_TAG_COMPONENT, dragonTag); // Serializing the dragons data, including its id.
            dragon.discard();
            player.setItemInHand(hand, stack);
            world.playSound(null, player.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.AMBIENT, 1, 1);
        }
        else // Client side Aesthetics
        {
            double width = dragon.getBbWidth();
            for (int i = 0; i <= Math.floor(width) * 25; ++i)
            {
                double calcX = Mth.cos(i + 360 / Mafs.PI * 360f) * (width * 1.5);
                double calcZ = Mth.sin(i + 360 / Mafs.PI * 360f) * (width * 1.5);
                double x = dragon.getX() + calcX;
                double y = dragon.getY() + (dragon.getBbHeight() * 1.8);
                double z = dragon.getZ() + calcZ;
                double xMot = -calcX / 5f;
                double yMot = -(dragon.getBbHeight() / 8);
                double zMot = -calcZ / 5f;

                world.addParticle(ParticleTypes.END_ROD, x, y, z, xMot, yMot, zMot);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack stack = context.getItemInHand();
        if (!containsDragon(stack)) return InteractionResult.PASS;
        Level world = context.getLevel();
        Player player = context.getPlayer();
        if (!stack.get(WRDataComponentTypes.DRAGON_TAG_COMPONENT).getUUID("Owner").equals(player.getUUID()))
        {
            player.sendSystemMessage(Component.translatable("item.wyrmroost.soul_crystal.not_owner").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        TamableAnimal dragon = getContained(stack, world);
        BlockPos pos = context.getClickedPos().offset(context.getClickedFace().getNormal());

        dragon.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5); // update the position now for collision checking
        dragon.setYRot(dragon.getYRot());
        dragon.setXRot(dragon.getXRot());
        if (!world.isUnobstructed(dragon, Shapes.create(dragon.getBoundingBox()))) {
            player.sendSystemMessage(Component.translatable("item.wyrmroost.soul_crystal.fail").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        if (!world.isClientSide()) // Spawn the entity on the server side only
        {
            stack.remove(WRDataComponentTypes.DRAGON_TAG_COMPONENT);
            world.addFreshEntity(dragon);
            world.playSound(null, dragon.getOnPos(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.AMBIENT, 1, 1);
        }
        else // Client Side Aesthetics
        {
            EntityDimensions size = dragon.getDimensions(dragon.getPose());

            double posX = pos.getX() + 0.5d;
            double posY = pos.getY() + (size.height() / 2);
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

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        if (containsDragon(stack))
        {
            CompoundTag tag = stack.get(WRDataComponentTypes.DRAGON_TAG_COMPONENT);
            Component name;

            if (tag.contains("CustomName"))
                name = Component.Serializer.fromJson(tag.getString("CustomName"), context.registries());
            else name = EntityType.byString(tag.getString("id")).orElse(null).getDescription();

            tooltip.add(name.copy().withStyle(ChatFormatting.BOLD));
            tooltip.add(Component.literal("Tamed by ").append(Component.literal(tag.getString("OwnerName")).withStyle(ChatFormatting.ITALIC)));
        }
    }

    @Override
    public Component getName(ItemStack stack)
    {
        Component name = super.getName(stack);
        if (containsDragon(stack)) name.copy().withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC);
        return name;
    }

    @Override
    public boolean useOnRelease(ItemStack stack)
    {
        return containsDragon(stack);
    }

    private static boolean containsDragon(ItemStack stack) {
        return stack.has(WRDataComponentTypes.DRAGON_TAG_COMPONENT);
    }

    @Nullable
    private static TamableAnimal getContained(ItemStack stack, Level world) {
        if (!containsDragon(stack)) return null;
        CompoundTag tag = stack.get(WRDataComponentTypes.DRAGON_TAG_COMPONENT);
        EntityType<?> type = EntityType.byString(tag.getString("id")).orElse(null);
        if (type == null) return null;
        TamableAnimal dragon = (TamableAnimal) type.create(world);
        dragon.load(tag);
        return dragon;
    }

    private static boolean isSuitableEntity(LivingEntity entity) {
        if (entity instanceof TamableAnimal) {
            if (entity instanceof AbstractDragonEntity) return true;
            ResourceLocation rl = EntityType.getKey(entity.getType());
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
