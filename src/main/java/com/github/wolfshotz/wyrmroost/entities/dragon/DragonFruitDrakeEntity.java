package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.DragonBreedGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.MoveToHomeGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRFollowOwnerGoal;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindPacket;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class DragonFruitDrakeEntity extends AbstractDragonEntity implements IShearable
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
        registerDataEntry("Gender", EntityDataEntry.BOOLEAN, GENDER);
        registerDataEntry("Sleeping", EntityDataEntry.BOOLEAN, SLEEPING);
        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT);
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
            { setFlags(EnumSet.of(Flag.MOVE)); }

            @Override
            public boolean canUse()
            {
                return !isTame() && super.canUse();
            }
        });
        goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(11, new LookAtPlayerGoal(this, LivingEntity.class, 7f));
        goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, temptGoal = new TemptGoal(this, 1d, Ingredient.of(WRItems.Tags.DRAGON_FRUIT_DRAKE_FOOD), false)
        {
            @Override
            public boolean canUse()
            {
                return !isTame() && isBaby() && super.canUse();
            }
        });

        targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(1, new NonTameRandomTargetGoal(this, Player.class, true, EntitySelector.NO_CREATIVE_OR_SPECTATOR)
        {
            @Override
            public boolean canUse()
            {
                return !isBaby() && super.canUse();
            }
        });
    }

    @Override
    public InteractionResult actuallyInteractWithMob(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Tags.Items.TOOLS_SHEAR) && hasControllingPassenger())
            return InteractionResult.sidedSuccess(level().isClientSide());

        if (!isTame() && isBaby() && isFoodItem(stack)) {
            if (!level().isClientSide() && temptGoal.isRunning())
            {
                tame(random.nextDouble() <= 0.2d, player);
                eat(stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        if (isTame() && stack.is(WRItems.Tags.ACTIVATES_DRAGON_FRUIT_DRAKE_CROPS_GROWTH) && growCropsTime <= 0) {
            eat(stack);
            growCropsTime = CROP_GROWTH_TIME;
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return super.actuallyInteractWithMob(player, hand);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        sitTimer.add((isInSittingPose() || isSleeping())? 0.1f : -0.1f);
        sleepTimer.add(isSleeping()? 0.05f : -0.1f);

        if (!level().isClientSide())
        {
            setSprinting(getTarget() != null);
            if (shearCooldownTime > 0) --shearCooldownTime;
            if (napTime > 0) --napTime;

            if (growCropsTime >= 0)
            {
                --growCropsTime;
                if (random.nextBoolean())
                {
                    AABB aabb = getBoundingBox().inflate(CROP_GROWTH_RADIUS);
                    int x = Mth.nextInt(random, (int) aabb.minX, (int) aabb.maxX);
                    int y = Mth.nextInt(random, (int) aabb.minY, (int) aabb.maxY);
                    int z = Mth.nextInt(random, (int) aabb.minZ, (int) aabb.maxZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level().getBlockState(pos);
                    Block block = state.getBlock();
                    if (block instanceof BonemealableBlock && !(block instanceof GrassBlock))
                    {
                        BonemealableBlock plant = (BonemealableBlock) block;
                        if (plant.isValidBonemealTarget(level(), pos, state))
                        {
                            plant.performBonemeal((ServerLevel) level(), random, pos, state);
                            level().levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0);
                        }
                    }
                }
            }

            if (!isBaby() && level().isDay() && !isSleeping() && isIdling() && random.nextDouble() < 0.002)
            {
                napTime = 1200;
                setSleeping(true);
            }
        }

        if (getAnimation() == BITE_ANIMATION && getAnimationTick() == 7 && hasControllingPassenger()) {
            attackInBox(getOffsetBox(getBbWidth()));
            AABB aabb = getBoundingBox().inflate(2).move(Mafs.getYawVec(yHeadRot, 0, 2));
            for (BlockPos pos : ModUtils.getBlockPosesInAABB(aabb)) {
                if (level().getBlockState(pos).getBlock() instanceof BushBlock)
                    level().destroyBlock(pos, true, this);
            }
        }
    }

    @Override
    public boolean isFoodItem(ItemStack stack)
    {
        return stack.is(WRItems.Tags.DRAGON_FRUIT_DRAKE_FOOD);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(WRItems.Tags.DRAGON_FRUIT_DRAKE_BREEDING_ITEMS);
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (key == KeybindPacket.MOUNT_KEY1 && pressed) setAnimation(BITE_ANIMATION);
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || isSleeping()) size = size.scale(1, 0.7f);
        return size.withEyeHeight(getBbHeight());
    }

    public Vec3 getPassengerPosOffset(Entity entity, int index) {
        return new Vec3(0, -0.25, 0);
    }

    @Override
    public boolean isShearable(@Nullable Player player, ItemStack item, Level level, BlockPos po)
    {
        return shearCooldownTime <= 0;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos)
    {
        playSound(SoundEvents.MOOSHROOM_SHEAR, 1f, 1f);
        shearCooldownTime = 12000;
        Holder<Enchantment> fortune = world.holderOrThrow(Enchantments.FORTUNE);
        return Collections.singletonList(new ItemStack(Items.APPLE, 1 + item.getEnchantmentLevel(fortune) + random.nextInt(2)));
    }

    @Override
    public void setMountCameraAngles(boolean backView, CalculateDetachedCameraDistanceEvent event)
    {
        if (backView) event.getCamera().move(-0.25f, 0.5f, 0);
        else event.getCamera().move(-1.5f, 0.15f, 0);
    }

    @Override
    public void swing(InteractionHand hand)
    {
        super.swing(hand);
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
        return random.nextDouble() < 0.01? -1 : 0;
    }

    @Override
    protected boolean canRide(Entity passenger)
    {
        return !isBaby() && passenger instanceof LivingEntity && isOwnedBy((LivingEntity) passenger);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_DFD_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_DFD_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_DFD_DEATH.value();
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {NO_ANIMATION, BITE_ANIMATION};
    }

    @Override
    public boolean hasGender() {
        return true;
    }

    @Override
    public boolean mayFly() {
        return false;
    }

    @Override
    public boolean maySleep() {
        return true;
    }

    @Override
    public boolean hasVariants() {
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data)
    {
        if (data == null)
        {
            data = new AgeableMobGroupData(true);
            if (reason == MobSpawnType.NATURAL) setAge(DragonEggProperties.get(getType()).getGrowthTime()); // set the first spawning dfd as a baby. the rest of the group will spawn as an adult.
        }

        return super.finalizeSpawn(world, difficulty, reason, data);
    }

    public static <F extends Mob> boolean getSpawnPlacement(EntityType<F> fEntityType, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random)
    {
        BlockState state = world.getBlockState(pos.below());
        return state.is(Blocks.GRASS_BLOCK) || (state.is(BlockTags.LEAVES) && pos.getY() < world.getSeaLevel() + 13) && world.getRawBrightness(pos, 0) > 8;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractDragonEntity.createDragonAttributes()
                .add(Attributes.MAX_HEALTH, 15)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 3);
    }

    public static boolean isCrop(Block block)
    {
        return block instanceof BonemealableBlock && !(block instanceof GrassBlock);
    }

    // todo: completely remake this so it instead looks for random block in range instead of closest,and checks to see if block is in range rather than being directly ontop of it
    private class MoveToCropsGoal extends MoveToBlockGoal
    {
        public MoveToCropsGoal()
        {
            super(DragonFruitDrakeEntity.this, 1, CROP_GROWTH_RADIUS * 2);
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            return growCropsTime >= 0 && findNearestBlock();
        }

        @Override
        protected int nextStartTick(PathfinderMob creature)
        {
            return 100;
        }

        @Override
        public boolean canContinueToUse()
        {
            return growCropsTime >= 0;
        }

        @Override
        public void tick()
        {
            super.tick();
            getLookControl().setLookAt(blockPos.getX(), blockPos.getY(), blockPos.getY());
            if (tryTicks >= 200 && random.nextInt(tryTicks) >= 100)
            {
                tryTicks = 0;
                findNearestBlock();
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader world, BlockPos pos)
        {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            return !pos.equals(blockPos) && isCrop(block) && ((BonemealableBlock) block).isValidBonemealTarget(world, pos, state);
        }
    }
}
