package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.DragonBreedGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.MoveToHomeGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRFollowOwnerGoal;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindPacket;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static net.minecraft.entity.ai.attributes.Attributes.*;

import ActionResultType;
import EntitySize;
import ILivingEntityData;
import MobEntity;
import SoundEvent;

public class DragonFruitDrakeEntity extends AbstractDragonEntity implements IForgeShearable
{
    private static final int CROP_GROWTH_RADIUS = 5;
    private static final int CROP_GROWTH_TIME = 1200; // 1 minute
    public static final Animation BITE_ANIMATION = new Animation(15);

    public final TickFloat sitTimer = new TickFloat().setLimit(0, 1);
    private int shearCooldownTime, napTime, growCropsTime;
    private TemptGoal temptGoal;

    public DragonFruitDrakeEntity(EntityType<? extends DragonFruitDrakeEntity> dragon, Level world)
    {
        super(dragon, world);

        registerDataEntry("ShearTimer", EntityDataEntry.INTEGER, () -> shearCooldownTime, v -> shearCooldownTime = v);
        registerDataEntry("Gender", EntityDataEntry.BOOLEAN, GENDER, getRNG().nextBoolean());
        registerDataEntry("Sleeping", EntityDataEntry.BOOLEAN, SLEEPING, false);
        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT, 0);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(3, new MoveToCropsGoal());
        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        goalSelector.addGoal(5, new DragonBreedGoal(this));
        goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.3, false));
        goalSelector.addGoal(8, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(9, new FollowParentGoal(this, 1)
        {
            { setMutexFlags(EnumSet.of(Flag.MOVE)); }

            @Override
            public boolean shouldExecute()
            {
                return !isTame() && super.shouldExecute();
            }
        });
        goalSelector.addGoal(10, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(11, new LookAtGoal(this, LivingEntity.class, 7f));
        goalSelector.addGoal(12, new LookRandomlyGoal(this));
        goalSelector.addGoal(7, temptGoal = new TemptGoal(this, 1d, false, Ingredient.fromItems(Items.APPLE))
        {
            @Override
            public boolean shouldExecute()
            {
                return !isTame() && isChild() && super.shouldExecute();
            }
        });

        targetSelector.addGoal(0, new HurtByTargetGoal(this).setCallsForHelp());
        targetSelector.addGoal(1, new NonTamedTargetGoal<Player>(this, Player.class, true, EntityPredicates.CAN_AI_TARGET::test)
        {
            @Override
            public boolean shouldExecute()
            {
                return !isChild() && super.shouldExecute();
            }
        });
    }

    @Override
    public ActionResultType playerInteraction(Player player, InteractionHand hand, ItemStack stack)
    {
        if (stack.getItem() == Items.SHEARS && canBeSteered())
            return ActionResultType.func_233537_a_(world.isRemote);

        if (!isTame() && isChild() && isFoodItem(stack))
        {
            if (!world.isRemote && temptGoal.isRunning())
            {
                tame(getRNG().nextDouble() <= 0.2d, player);
                eat(stack);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.CONSUME;
        }

        if (isTame() && stack.getItem() == Items.GLISTERING_MELON_SLICE && growCropsTime <= 0)
        {
            eat(stack);
            growCropsTime = CROP_GROWTH_TIME;
            return ActionResultType.func_233537_a_(world.isRemote);
        }

        return super.playerInteraction(player, hand, stack);
    }

    @Override
    public void livingTick()
    {
        super.livingTick();

        sitTimer.add((func_233684_eK_() || isSleeping())? 0.1f : -0.1f);
        sleepTimer.add(isSleeping()? 0.05f : -0.1f);

        if (!world.isRemote)
        {
            setSprinting(getAttackTarget() != null);
            if (shearCooldownTime > 0) --shearCooldownTime;
            if (napTime > 0) --napTime;

            if (growCropsTime >= 0)
            {
                --growCropsTime;
                if (getRNG().nextBoolean())
                {
                    AxisAlignedBB aabb = getBoundingBox().grow(CROP_GROWTH_RADIUS);
                    int x = Mth.nextInt(getRNG(), (int) aabb.minX, (int) aabb.maxX);
                    int y = Mth.nextInt(getRNG(), (int) aabb.minY, (int) aabb.maxY);
                    int z = Mth.nextInt(getRNG(), (int) aabb.minZ, (int) aabb.maxZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block instanceof IGrowable && !(block instanceof GrassBlock))
                    {
                        IGrowable plant = (IGrowable) block;
                        if (plant.canGrow(level, pos, state, false))
                        {
                            plant.grow((ServerWorld) level, getRNG(), pos, state);
                            level.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 0);
                        }
                    }
                }
            }

            if (!isChild() && level.isDaytime() && !isSleeping() && isIdling() && getRNG().nextDouble() < 0.002)
            {
                napTime = 1200;
                setSleeping(true);
            }
        }

        if (getAnimation() == BITE_ANIMATION && getAnimationTick() == 7 && canPassengerSteer())
        {
            attackInBox(getOffsetBox(getBbWidth()));
            AxisAlignedBB aabb = getBoundingBox().grow(2).offset(Mafs.getYawVec(rotationYawHead, 0, 2));
            for (BlockPos pos : ModUtils.getBlockPosesInAABB(aabb))
            {
                if (level.getBlockState(pos).getBlock() instanceof BushBlock)
                    level.destroyBlock(pos, true, this);
            }
        }
    }

    @Override
    public boolean isFoodItem(ItemStack stack)
    {
        return stack.getItem().isIn(Tags.Items.CROPS) || ModUtils.equalsAny(stack.getItem(), Items.APPLE, Items.SWEET_BERRIES, Items.MELON, Items.GLISTERING_MELON_SLICE);
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (key == KeybindPacket.MOUNT_KEY1 && pressed) setAnimation(BITE_ANIMATION);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return getBbHeight();
    }

    @Override
    public EntitySize getSize(Pose poseIn)
    {
        EntitySize size = getType().getSize().scale(getRenderScale());
        if (func_233684_eK_() || isSleeping()) size = size.scale(1, 0.7f);
        return size;
    }

    @Override
    public double getMountedYOffset()
    {
        return super.getMountedYOffset() + 0.1d;
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, Level world, BlockPos pos)
    {
        return shearCooldownTime <= 0;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos, int fortune)
    {
        playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1f, 1f);
        shearCooldownTime = 12000;
        return Collections.singletonList(new ItemStack(Items.APPLE, 1 + fortune + getRNG().nextInt(2)));
    }

    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
        if (backView) event.getInfo().movePosition(-0.25d, 0.5d, 0);
        else event.getInfo().movePosition(-1.5, 0.15, 0);
    }

    @Override
    public void swingArm(InteractionHand hand)
    {
        super.swingArm(hand);
        setAnimation(BITE_ANIMATION);
    }

    @Override
    public boolean canFly()
    {
        return false;
    }

    @Override
    public boolean shouldSleep()
    {
        return napTime <= 0 && super.shouldSleep();
    }

    @Override
    public int determineVariant()
    {
        return getRNG().nextDouble() < 0.01? -1 : 0;
    }

    @Override
    protected boolean canBeRidden(Entity passenger)
    {
        return !isChild() && passenger instanceof LivingEntity && isOwnedBy((LivingEntity) passenger);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_DFD_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_DFD_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_DFD_DEATH.get();
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem() == Items.APPLE;
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {NO_ANIMATION, BITE_ANIMATION};
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT dataTag)
    {
        if (data == null)
        {
            data = new AgeableData(true);
            if (reason == MobSpawnType.NATURAL) setGrowingAge(DragonEggProperties.get(getType()).getGrowthTime()); // set the first spawning dfd as a baby. the rest of the group will spawn as an adult.
        }

        return super.onInitialSpawn(world, difficulty, reason, data, dataTag);
    }

    public static <F extends MobEntity> boolean getSpawnPlacement(EntityType<F> fEntityType, IServerWorld world, MobSpawnType spawnReason, BlockPos pos, Random random)
    {
        BlockState state = world.getBlockState(pos.below());
        return state.is(Blocks.GRASS_BLOCK) || (state.is(BlockTags.LEAVES) && pos.getY() < world.getSeaLevel() + 13) && world.getLightSubtracted(pos, 0) > 8;
    }

    public static void setSpawnBiomes(BiomeLoadingEvent event)
    {
        if (event.getCategory() == Biome.Category.JUNGLE)
            event.getSpawns().func_242575_a(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(WREntities.DRAGON_FRUIT_DRAKE.get(), 23, 4, 5));
    }

    public static AttributeModifierMap.MutableAttribute getAttributes()
    {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(MAX_HEALTH, 15)
                .createMutableAttribute(MOVEMENT_SPEED, 0.23)
                .createMutableAttribute(ATTACK_DAMAGE, 3);
    }

    public static boolean isCrop(Block block)
    {
        return block instanceof IGrowable && !(block instanceof GrassBlock);
    }

    // todo: completely remake this so it instead looks for random block in range instead of closest,and checks to see if block is in range rather than being directly ontop of it
    private class MoveToCropsGoal extends MoveToBlockGoal
    {
        public MoveToCropsGoal()
        {
            super(DragonFruitDrakeEntity.this, 1, CROP_GROWTH_RADIUS * 2);
            setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean shouldExecute()
        {
            return growCropsTime >= 0 && searchForDestination();
        }

        @Override
        protected int getRunDelay(CreatureEntity creature)
        {
            return 100;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return growCropsTime >= 0;
        }

        @Override
        public void tick()
        {
            super.tick();
            getLookController().setLookPosition(destinationBlock.getX(), destinationBlock.getY(), destinationBlock.getY());
            if (timeoutCounter >= 200 && getRNG().nextInt(timeoutCounter) >= 100)
            {
                timeoutCounter = 0;
                searchForDestination();
            }
        }

        @Override
        protected boolean shouldMoveTo(IWorldReader world, BlockPos pos)
        {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            return !pos.equals(destinationBlock) && isCrop(block) && ((IGrowable) block).canGrow(world, pos, state, false);
        }
    }
}
