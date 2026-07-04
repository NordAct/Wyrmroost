package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.entities.dragon.LDWyrmEntity;
import com.github.wolfshotz.wyrmroost.registry.WRDataComponentTypes;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class LDWyrmItem extends Item {
    public static final String DATA_CONTENTS = "DesertWyrm"; // Should ALWAYS be a compound. If it throws a cast class exception SOMETHING fucked up.

    public LDWyrmItem() {
        super(WRItems.builder());

        //DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientEvents.CALLBACKS.add(() -> ItemModelsProperties.func_239418_a_(this, Wyrmroost.rl("is_alive"), (stack, world, player) ->
        //{
        //    if (stack.hasTag() && stack.getTag().contains(DATA_CONTENTS)) return 1f;
        //    return 0f;
        //}))); //todo model predicate - Nord
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.has(WRDataComponentTypes.DRAGON_TAG_COMPONENT)) {
            Level world = context.getLevel();
            if (!world.isClientSide()) {
                BlockPos pos = context.getClickedPos().offset(context.getClickedFace().getNormal());
                CompoundTag contents = stack.get(WRDataComponentTypes.DRAGON_TAG_COMPONENT);
                LDWyrmEntity entity = (LDWyrmEntity) WREntities.LESSER_DESERTWYRM.value().create(world);

                entity.load(contents);
                if (stack.has(DataComponents.CUSTOM_NAME))
                    entity.setCustomName(stack.get(DataComponents.CUSTOM_NAME)); // Item name takes priority
                entity.setPos(pos.getX(), pos.getY(), pos.getZ());
                world.addFreshEntity(entity);
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}

