package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.entities.dragon.CoinDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WRDataComponentTypes;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import java.util.Random;

public class CoinDragonItem extends Item
{
    public static final String DATA_ENTITY = "CoinDragonData";
    public static final ResourceLocation VARIANT_OVERRIDE = Wyrmroost.rl("variant");

    public CoinDragonItem()
    {
        super(WRItems.builder().stacksTo(1));
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ClientEvents.CALLBACKS.add(() -> {
                ItemProperties.register(this, VARIANT_OVERRIDE, (s, w, p, seed) -> {
                    if (!s.has(WRDataComponentTypes.DRAGON_TAG_COMPONENT)) return 0;
                    return s.get(WRDataComponentTypes.DRAGON_TAG_COMPONENT).getInt(CoinDragonEntity.DATA_VARIANT);
                });
            });
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public InteractionResult useOn(UseOnContext context)
    {
        Level world = context.getLevel();
        CoinDragonEntity entity = (CoinDragonEntity) WREntities.COIN_DRAGON.value().create(context.getLevel());
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (!stack.has(WRDataComponentTypes.DRAGON_TAG_COMPONENT)) {
            entity.setVariant(entity.getRandom().nextInt(5));
        }

        if (!world.isClientSide() && stack.has(WRDataComponentTypes.DRAGON_TAG_COMPONENT)) {
            CompoundTag tag = stack.get(WRDataComponentTypes.DRAGON_TAG_COMPONENT);
            entity.readAdditionalSaveData(tag);
            if (stack.has(DataComponents.CUSTOM_NAME)) entity.setCustomName(stack.getDisplayName()); // set entity name from stack name
        }

        entity.setPos(pos.getCenter());
        if (!world.noCollision(entity)) {
            if (world.isClientSide()) player.sendSystemMessage(Component.translatable("item.wyrmroost.soul_crystal.fail").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        if (!player.isCreative() || stack.has(WRDataComponentTypes.DRAGON_TAG_COMPONENT))
            player.setItemInHand(context.getHand(), ItemStack.EMPTY);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setYRot(entity.yHeadRot = player.yHeadRot + 180);
        world.addFreshEntity(entity);
        return InteractionResult.SUCCESS;
    }

    public static LootPoolEntryContainer.Builder<?> getLootEntry()
    {
        CompoundTag parent = new CompoundTag();
        CompoundTag child = new CompoundTag(); // because the parent nbt gets merged with the stack, we need to nest a child within the one getting merged
        child.putInt(CoinDragonEntity.DATA_VARIANT, new Random().nextInt(5));
        parent.put(DATA_ENTITY, child);
        return LootItem.lootTableItem(WRItems.COIN_DRAGON.value()).apply(SetComponentsFunction.setComponent(WRDataComponentTypes.DRAGON_TAG_COMPONENT.get(), child));
    }
}
