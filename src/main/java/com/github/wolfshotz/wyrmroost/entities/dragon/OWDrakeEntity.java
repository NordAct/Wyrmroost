package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.github.wolfshotz.wyrmroost.containers.util.SlotBuilder;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInvHandler;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.items.DragonArmorItem;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindPacket;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.common.Tags;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

/**
 * Created by com.github.WolfShotz 7/10/19 - 22:18
 */
public class OWDrakeEntity extends AbstractDragonEntity
{
    // inventory slot constants
    public static final int SADDLE_SLOT = 0;
    public static final int ARMOR_SLOT = 1;
    public static final int CHEST_SLOT = 2;

    // Dragon Entity Data
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(OWDrakeEntity.class, EntityDataSerializers.BOOLEAN);

    // Dragon Entity Animations
    public static final Animation GRAZE_ANIMATION = new Animation(35);
    public static final Animation HORN_ATTACK_ANIMATION = new Animation(15);
    public static final Animation ROAR_ANIMATION = new Animation(86);

    public final TickFloat sitTimer = new TickFloat().setLimit(0, 1);
    public LivingEntity thrownPassenger;

    public OWDrakeEntity(EntityType<? extends OWDrakeEntity> drake, Level world)
    {
        super(drake, world);

        registerDataEntry("Sleeping", EntityDataEntry.BOOLEAN, SLEEPING);
        registerDataEntry("Gender", EntityDataEntry.BOOLEAN, GENDER);
        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        goalSelector.addGoal(5, new ControlledAttackGoal(this, 1.425, true, d -> AnimationPacket.send(d, HORN_ATTACK_ANIMATION)));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 10f));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Player.class, true, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
    }

    // ================================
    //           Entity Data
    // ================================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(SADDLED, false);
    }

    public boolean hasChest()
    {
        return getStackInSlot(CHEST_SLOT) != ItemStack.EMPTY;
    }

    public boolean isSaddled()
    {
        return entityData.get(SADDLED);
    }

    @Override
    public int determineVariant()
    {
        if (random.nextDouble() < 0.008) return -1;
        if (level().getBiome(blockPosition()).is(Tags.Biomes.IS_SAVANNA)) return 1;
        return 0;
    }

    @Override
    public DragonInvHandler createInv()
    {
        return new DragonInvHandler(this, 24);
    }

    // ================================

    @Override
    public void aiStep()
    {
        super.aiStep();

        sitTimer.add((isInSittingPose() || isSleeping())? 0.1f : -0.1f);
        sleepTimer.add(isSleeping()? 0.04f : -0.06f);

        if (thrownPassenger != null)
        {
            thrownPassenger.setDeltaMovement(Mafs.nextDouble(random), 0.1 + random.nextDouble(), Mafs.nextDouble(random));
            ((ServerChunkCache) level().getChunkSource()).broadcastAndSend(thrownPassenger, new ClientboundSetEntityMotionPacket(thrownPassenger)); // notify client
            thrownPassenger = null;
        }

        if (!level().isClientSide() && getTarget() == null && !isInSittingPose() && !isSleeping() && level().getBlockState(blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK && random.nextDouble() < (isBaby() || getHealth() < getMaxHealth()? 0.005 : 0.001))
            AnimationPacket.send(this, GRAZE_ANIMATION);

        Animation animation = getAnimation();
        int tick = getAnimationTick();
        LivingEntity target = getTarget();

        if (animation == ROAR_ANIMATION)
        {
            if (tick == 0) playSound(WRSounds.ENTITY_OWDRAKE_ROAR.value(), 3f, 1f, true);
            else if (tick == 15)
            {
                for (LivingEntity e : getEntitiesNearby(15, e -> !isAlliedTo(e))) // Dont get too close now ;)
                {
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200));
                    if (distanceToSqr(e) <= 10)
                    {
                        double angle = Mafs.getAngle(getX(), getZ(), e.getX(), e.getZ()) * Math.PI / 180;
                        e.push(1.2 * -Math.cos(angle), 0.4d, 1.2 * -Math.sin(angle));
                    }
                }
            }
        }

        if (animation == HORN_ATTACK_ANIMATION)
        {
            if (tick == 8)
            {
                if (target != null) setYRot(yBodyRot = (float) Mafs.getAngle(this, target) + 90f);
                playSound(SoundEvents.IRON_GOLEM_ATTACK, 1, 0.5f, true);
                AABB box = getOffsetBox(getBbWidth()).inflate(-0.075);
                attackInBox(box);
                for (BlockPos pos : ModUtils.getBlockPosesInAABB(box))
                {
                    if (level().getBlockState(pos).is(BlockTags.LEAVES))
                        level().destroyBlock(pos, false, this);
                }
            }
        }

        if (!level().isClientSide() && animation == GRAZE_ANIMATION && tick == 13)
        {
            Vec3 vec3pos = Mafs.getYawVec(yBodyRot, 0, getBbWidth() / 2 + 1).add(position());
            BlockPos pos = new BlockPos((int) vec3pos.x, (int) vec3pos.y, (int) vec3pos.z);
            if (level().getBlockState(pos).is(Blocks.SHORT_GRASS) && WRConfig.canGrief(level()))
            {
                level().destroyBlock(pos, false);
                ate();
            }
            else if (level().getBlockState(pos = pos.below()).getBlock() == Blocks.GRASS_BLOCK)
            {
                level().levelEvent(2001, pos, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                level().setBlock(pos, Blocks.DIRT.defaultBlockState(), 2);
                ate();
            }
        }
    }

    public Vec3 getPassengerPosOffset(Entity entity, int index) {
        return new Vec3(0, -0.5, 0);
    }

    @Override
    public InteractionResult actuallyInteractWithMob(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.SADDLE && !isSaddled() && !isBaby())
        {
            if (!level().isClientSide())
            {
                getInvHandler().insertItem(SADDLE_SLOT, stack.copy(), false);
                usePlayerItem(player, hand, stack);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        if (!isTame() && isBaby() && isFoodItem(stack))
        {
            tame(random.nextInt(10) == 0, player);
            usePlayerItem(player, hand, stack);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return super.actuallyInteractWithMob(player, hand);
    }

    @Override
    public void positionRider(Entity entity, MoveFunction callback)
    {
        super.positionRider(entity, callback);

        if (entity instanceof LivingEntity)
        {
            LivingEntity passenger = ((LivingEntity) entity);
            if (isTame()) setSprinting(passenger.isSprinting());
            else if (!level().isClientSide() && passenger instanceof Player)
            {
                double rng = random.nextDouble();

                if (rng < 0.01) tame(true, (Player) passenger);
                else if (rng <= 0.1)
                {
                    setTarget(passenger);
                    boardingCooldown = 60;
                    ejectPassengers();
                    thrownPassenger = passenger; // needs to be queued for next tick otherwise some voodoo shit breaks the throwing off logic >.>
                }
            }
        }
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == SADDLE_SLOT)
        {
            entityData.set(SADDLED, !stack.isEmpty());
            if (!stack.isEmpty() && !onLoad) playSound(SoundEvents.HORSE_SADDLE, 1, 1);
        }

        if (slot == ARMOR_SLOT) setArmor(stack);
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (key == KeybindPacket.MOUNT_KEY1 && pressed && noActiveAnimation())
        {
            if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) setAnimation(ROAR_ANIMATION);
            else setAnimation(HORN_ATTACK_ANIMATION);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || isSleeping()) size = size.scale(1, 0.75f);
        return size;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addScreenInfo(StaffScreen screen)
    {
        screen.addAction(StaffAction.INVENTORY);
        screen.addAction(StaffAction.TARGET);
        super.addScreenInfo(screen);
    }

    @Override
    public void addContainerInfo(DragonInvContainer container)
    {
        super.addContainerInfo(container);

        DragonInvHandler inv = container.inventory;

        container.addSlot(new SlotBuilder(inv, SADDLE_SLOT, 17, 45).only(Items.SADDLE));
        container.addSlot(new SlotBuilder(inv, ARMOR_SLOT, 17, 63).only(DragonArmorItem.class));
        container.addSlot(new SlotBuilder(inv, CHEST_SLOT, 17, 81).only(ChestBlock.class).limit(1).canTake(p -> inv.isEmptyAfter(CHEST_SLOT)));
        container.makeSlots(3, 51, 45, 7, 3, (i, x, z) -> new SlotBuilder(inv, i, x, z).condition(this::hasChest));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target)
    {
        LivingEntity prev = getTarget();

        super.setTarget(target);

        boolean flag = getTarget() != null;
        setSprinting(flag);

        if (flag && prev != target && target.getType() == EntityType.PLAYER && !isTame() && noActiveAnimation())
            AnimationPacket.send(OWDrakeEntity.this, OWDrakeEntity.ROAR_ANIMATION);
    }

    @Override
    protected boolean isImmobile()
    {
        return getAnimation() == ROAR_ANIMATION || super.isImmobile();
    }

    @Override
    public void setMountCameraAngles(boolean backView, CalculateDetachedCameraDistanceEvent event)
    {
        if (backView) event.getCamera().move(-0.5f, 0.75f, 0);
        else event.getCamera().move(-3, 0.3f, 0);
    }

    @Override
    public void ate()
    {
        if (isBaby()) ageUp(60);
        if (getHealth() < getMaxHealth()) heal(4f);
    }

    @Override
    protected boolean canRide(Entity entity)
    {
        return isSaddled() && !isBaby() && (isOwnedBy((LivingEntity) entity) || (!isTame() && boardingCooldown <= 0));
    }

    @Override
    public float getTravelSpeed()
    {
        float speed = (float) getAttributeValue(Attributes.MOVEMENT_SPEED);
        if (hasControllingPassenger()) speed += 0.45f;
        return speed;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        playSound(SoundEvents.COW_STEP, 0.3f, 1f);
        super.playStepSound(pos, blockIn);
    }

    @Override
    public boolean defendsHome()
    {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_OWDRAKE_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_OWDRAKE_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_OWDRAKE_DEATH.value();
    }

    @Override
    public boolean canFly()
    {
        return false;
    }

    @Override
    public boolean isFoodItem(ItemStack stack)
    {
        return stack.is(WRItems.Tags.OVERWORLD_DRAKE_FOOD);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(WRItems.Tags.OVERWORLD_DRAKE_BREEDING_ITEMS);
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {NO_ANIMATION, GRAZE_ANIMATION, HORN_ATTACK_ANIMATION, ROAR_ANIMATION};
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

    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractDragonEntity.createDragonAttributes()
                .add(Attributes.MAX_HEALTH, 70)
                .add(Attributes.MOVEMENT_SPEED, 0.2125)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.ATTACK_KNOCKBACK, 2.85)
                .add(Attributes.ATTACK_DAMAGE, 8);
    }
}
