package com.github.wolfshotz.wyrmroost.items.staff;

import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.render.RenderHelper;
import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

public enum StaffAction
{
    DEFAULT
            {
                @Override
                public boolean rightClick(AbstractDragonEntity dragon, Player player, ItemStack stack)
                {
                    if (player.level().isClientSide()) StaffScreen.open(dragon);
                    return true;
                }
            },

    INVENTORY
            {
                @Override
                public void onSelected(AbstractDragonEntity dragon, Player player, ItemStack stack)
                {
                    DragonStaffItem.setAction(DEFAULT, player, stack);
                    if (player instanceof ServerPlayer serverPlayer)
                        serverPlayer.openMenu(DragonInvContainer.getProvider(dragon), b -> b.writeInt(dragon.getId()));
                }
            },

    SIT
            {
                @Override
                public void onSelected(AbstractDragonEntity dragon, Player player, ItemStack stack)
                {
                    dragon.setSit(!dragon.isInSittingPose());
                    DragonStaffItem.setAction(DEFAULT, player, stack);
                }

                @Override
                public String getTranslateKey(@Nullable AbstractDragonEntity dragon)
                {
                    if (dragon != null && dragon.isInSittingPose()) return "item.wyrmroost.dragon_staff.action.sit.come";
                    return "item.wyrmroost.dragon_staff.action.sit.stay";
                }
            },

    HOME
            {
                @Override
                public void onSelected(AbstractDragonEntity dragon, Player player, ItemStack stack)
                {
                    if (dragon.getHomePos().isPresent())
                    {
                        dragon.clearHome();
                        DragonStaffItem.setAction(DEFAULT, player, stack);
                    }
                }

                @Override
                public boolean clickBlock(AbstractDragonEntity dragon, UseOnContext context)
                {
                    BlockPos pos = context.getClickedPos();
                    Level level = context.getLevel();
                    ItemStack stack = context.getItemInHand();
                    DragonStaffItem.setAction(DEFAULT, context.getPlayer(), stack);
                    if (level.getBlockState(pos).isSolid())
                    {
                        dragon.setHomePos(pos);
                        ModUtils.playLocalSound(level, pos, SoundEvents.BEEHIVE_ENTER, 1, 1);
                    }
                    else
                    {
                        ModUtils.playLocalSound(level, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, 1, 1);
                        for (int i = 0; i < 10; i++)
                            level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5d, pos.getY() + 1, pos.getZ() + 0.5d, 0, i * 0.025, 0);
                    }

                    return true;
                }

                @Override
                public void render(AbstractDragonEntity dragon, PoseStack ms, float partialTicks) {}

                @Override
                public String getTranslateKey(@Nullable AbstractDragonEntity dragon)
                {
                    if (dragon != null && dragon.getHomePos().isPresent())
                        return TRANSLATE_PATH + "home.remove";
                    return TRANSLATE_PATH + "home.set";
                }
            },

    TARGET
            {
                private static final int TARGET_RANGE = 40;

                @Override
                public void onSelected(AbstractDragonEntity dragon, Player player, ItemStack stack)
                {
                    dragon.clearAI();
                    dragon.clearHome();
                    dragon.setSit(false);
                }

                @Override
                public boolean rightClick(AbstractDragonEntity dragon, Player player, ItemStack stack)
                {
                    EntityHitResult ertr = rayTrace(player, dragon);
                    if (ertr != null)
                    {
                        dragon.setTarget((LivingEntity) ertr.getEntity());
                        if (player.level().isClientSide())
                            ModUtils.playLocalSound(player.level(), player.blockPosition(), SoundEvents.BLAZE_SHOOT, 1, 0.5f);
                        return true;
                    }
                    return false;
                }

                @Override
                public void render(AbstractDragonEntity dragon, PoseStack ms, float partialTicks)
                {
                    EntityHitResult rtr = rayTrace(ClientEvents.getPlayer(), dragon);
                    if (rtr != null && rtr.getEntity() != dragon.getTarget())
                        RenderHelper.renderEntityOutline(rtr.getEntity(), 255, 0, 0, (int) (Mth.cos((dragon.tickCount + partialTicks) * 0.2f) * 35 + 45));
                }

                @Nullable
                private EntityHitResult rayTrace(Player player, AbstractDragonEntity dragon)
                {
                    return Mafs.rayTraceEntities(player,
                            TARGET_RANGE,
                            e -> e instanceof LivingEntity && dragon.wantsToAttack((LivingEntity) e, dragon.getOwner()));
                }
            };

    public static final StaffAction[] VALUES = values(); // cache
    private static final String TRANSLATE_PATH = "item.wyrmroost.dragon_staff.action.";

    public boolean clickBlock(AbstractDragonEntity dragon, UseOnContext context) { return false; }

    public boolean rightClick(AbstractDragonEntity dragon, Player player, ItemStack stack) { return false; }

    public void onSelected(AbstractDragonEntity dragon, Player player, ItemStack stack) {}

    public void render(AbstractDragonEntity dragon, PoseStack ms, float partialTicks) {}

    protected String getTranslateKey(@Nullable AbstractDragonEntity dragon)
    {
        return TRANSLATE_PATH + name().toLowerCase();
    }

    public Component getTranslation(@Nullable AbstractDragonEntity dragon)
    {
        return Component.translatable(getTranslateKey(dragon));
    }
}