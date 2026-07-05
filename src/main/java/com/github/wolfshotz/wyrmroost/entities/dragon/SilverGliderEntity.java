package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.DragonBreedGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.FlyerWanderGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRAvoidEntityGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRFollowOwnerGoal;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.network.packets.SGGlidePacket;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SilverGliderEntity extends AbstractDragonEntity
{
    public final TickFloat sitTimer = new TickFloat().setLimit(0, 1);
    public final TickFloat flightTimer = new TickFloat().setLimit(0, 1);

    public TemptGoal temptGoal;
    public boolean isGliding; // controlled by player-gliding.

    public SilverGliderEntity(EntityType<? extends AbstractDragonEntity> dragon, Level world)
    {
        super(dragon, world);

        registerDataEntry("Gender", EntityDataEntry.BOOLEAN, GENDER, random.nextBoolean());
        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT, 0);
        registerDataEntry("Sleeping", EntityDataEntry.BOOLEAN, SLEEPING, false);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(3, temptGoal = new TemptGoal(this, 0.8d, Ingredient.of(WRItems.Tags.SILVER_GLIDER_FOOD), true));
        goalSelector.addGoal(4, new WRAvoidEntityGoal<>(this, Player.class, 10f, 0.8));
        goalSelector.addGoal(5, new DragonBreedGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new SwoopGoal());
        goalSelector.addGoal(8, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 7f));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (isGliding && !isRiding()) isGliding = false;

        sitTimer.add((isInSittingPose() || isSleeping())? 0.2f : -0.2f);
        sleepTimer.add(isSleeping()? 0.05f : -0.1f);
        flightTimer.add(isFlying() || isGliding()? 0.1f : -0.1f);
    }

    @Override
    public void rideTick()
    {
        super.rideTick();

        if (!(getVehicle() instanceof Player)) return;
        Player player = (Player) getVehicle();
        final boolean FLAG = shouldGlide(player);

        if (level().isClientSide() && isGliding != FLAG)
        {
            SGGlidePacket.send(FLAG);
            isGliding = FLAG;
        }

        if (isGliding)
        {
            Vec3 vec3d = player.getLookAngle().scale(0.3);
            player.setDeltaMovement(player.getDeltaMovement().scale(0.6).add(vec3d.x, Math.min(vec3d.y * 2, 0), vec3d.z));
            if (player instanceof ServerPlayer serverPlayer) serverPlayer.connection.clientIsFloating = false;
            player.fallDistance = 0;
        }
    }

    @Override
    public void travel(Vec3 vec3d)
    {
        Vec3 look = getLookAngle();
        if (isFlying() && look.y < 0) setDeltaMovement(getDeltaMovement().add(0, look.y * 0.25, 0));

        super.travel(vec3d);
    }

    @Override
    public InteractionResult actuallyInteractWithMob(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = super.actuallyInteractWithMob(player, hand);
        if (result.consumesAction()) return result;

        if (!isTame() && isFood(stack))
        {
            if (!level().isClientSide() && (temptGoal.isRunning() || player.isCreative()))
            {
                tame(random.nextDouble() < 0.333, player);
                eat(stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        if (isOwnedBy(player) && player.getPassengers().isEmpty() && !player.isShiftKeyDown() && !isFood(stack) && !isLeashed())
        {
            startRiding(player, true);
            setSit(false);
            clearAI();
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return InteractionResult.PASS;
    }

    public boolean shouldGlide(Player player)
    {
        if (isBaby()) return false;
        if (!player.jumping) return false;
        if (player.getAbilities().flying) return false;
        if (player.isFallFlying()) return false;
        if (player.isInWater()) return false;
        if (player.getDeltaMovement().y > 0) return false;
        if (isGliding() && !player.onGround()) return true;
        return getAltitude() - 1.8 > 4;
    }

    @Override
    public void doSpecialEffects()
    {
        if (getVariant() == -1 && tickCount % 5 == 0)
        {
            double x = getX() + random.nextGaussian();
            double y = getY() + random.nextDouble();
            double z = getZ() + random.nextGaussian();
            level().addParticle(new DustParticleOptions(new Vector3f(1f, 0.8f, 0), 1f), x, y, z, 0, 0.2f, 0);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || isSleeping()) size = size.scale(1, 0.87f);
        return size;
    }

    @Override
    public int determineVariant()
    {
        if (random.nextDouble() < 0.002) return -1;
        return random.nextInt(3);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_SILVERGLIDER_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_SILVERGLIDER_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_SILVERGLIDER_DEATH.value();
    }

    @Override
    public Vec3 getRidingPosOffset(int passengerIndex)
    {
        return new Vec3(0, 1.81, 0.5d);
    }

    @Override
    public boolean shouldFly()
    {
        return isRiding()? isGliding() : super.shouldFly();
    }

    @Override
    public int getMaxHeadXRot()
    {
        return 30;
    }

    @Override
    public int getYawRotationSpeed()
    {
        return isFlying()? 5 : 75;
    }

    public boolean isGliding()
    {
        return isGliding;
    }

    @Override
    public boolean isFoodItem(ItemStack stack) {
        return stack.is(WRItems.Tags.SILVER_GLIDER_FOOD);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(WRItems.Tags.SILVER_GLIDER_BREEDING_ITEMS);
    }

    @Override
    public boolean hasGender() {
        return true;
    }

    @Override
    public boolean mayFly() {
        return true;
    }

    @Override
    public boolean maySleep() {
        return true;
    }

    @Override
    public boolean hasVariants() {
        return true;
    }

    public static boolean getSpawnPlacement(EntityType<SilverGliderEntity> fEntityType, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos blockPos, RandomSource random)
    {
        if (spawnReason == MobSpawnType.SPAWNER) return true;
        BlockState block = world.getBlockState(blockPos.below());
        return block.isAir() || block.is(Tags.Blocks.SANDS) && world.getRawBrightness(blockPos, 0) > 8;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractDragonEntity.createDragonAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FLYING_SPEED, 0.12);
    }

    public class SwoopGoal extends Goal
    {
        private BlockPos pos;

        public SwoopGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            if (!isFlying()) return false;
            if (isRiding()) return false;
            if (random.nextDouble() > 0.001) return false;
            if (level().getFluidState(this.pos = level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPosition()).below()).isEmpty())
                return false;
            return getY() - pos.getY() > 8;
        }

        @Override
        public boolean canContinueToUse()
        {
            return blockPosition().distSqr(pos) > 8;
        }

        @Override
        public void tick()
        {
            if (getNavigation().isDone()) getNavigation().moveTo(pos.getX(), pos.getY() + 2, pos.getZ(), 1);
            getLookControl().setLookAt(pos.getX(), pos.getY() + 2, pos.getZ());
        }
    }
}
