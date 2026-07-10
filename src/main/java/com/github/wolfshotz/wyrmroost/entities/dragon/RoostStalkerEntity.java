package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.github.wolfshotz.wyrmroost.containers.util.SlotBuilder;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInvHandler;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.DefendHomeGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.DragonBreedGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.MoveToHomeGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRFollowOwnerGoal;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import com.github.wolfshotz.wyrmroost.network.packets.AddPassengerPacket;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class RoostStalkerEntity extends AbstractDragonEntity
{
    public static final int ITEM_SLOT = 0;
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(RoostStalkerEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> SCAVENGING = SynchedEntityData.defineId(RoostStalkerEntity.class, EntityDataSerializers.BOOLEAN);

    public RoostStalkerEntity(EntityType<? extends RoostStalkerEntity> stalker, Level world)
    {
        super(stalker, world);

        registerDataEntry("Sleeping", EntityDataEntry.BOOLEAN, SLEEPING);
        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(5, new MoveToHomeGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(9, new ScavengeGoal(1.1d));
        goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(11, new LookAtPlayerGoal(this, LivingEntity.class, 5f));
        goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        goalSelector.addGoal(8, new AvoidEntityGoal<>(this, Player.class, 7f, 1.15f, 1f)
        {
            @Override
            public boolean canUse()
            {
                return !isTame() && !getItem().isEmpty() && super.canUse();
            }
        });

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Chicken || target instanceof Rabbit || target instanceof Turtle));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(ITEM, ItemStack.EMPTY);
        builder.define(SCAVENGING, false);
    }

    public ItemStack getItem()
    {
        return entityData.get(ITEM);
    }

    private boolean hasItem()
    {
        return getItem() != ItemStack.EMPTY;
    }

    public void setItem(ItemStack item)
    {
        entityData.set(ITEM, item);
        if (!item.isEmpty()) playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 0.5f, 1);
    }

    public boolean isScavenging()
    {
        return entityData.get(SCAVENGING);
    }

    public void setScavenging(boolean b)
    {
        entityData.set(SCAVENGING, b);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        sleepTimer.add(isSleeping()? 0.08f : -0.15f);

        if (!level().isClientSide())
        {
            ItemStack item = getStackInSlot(ITEM_SLOT);
            if (isFood(item) && getHealth() < getMaxHealth() && random.nextDouble() <= 0.0075)
                eat(item);
        }
    }


    @Override
    public InteractionResult actuallyInteractWithMob(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        final InteractionResult COMMON_SUCCESS = InteractionResult.sidedSuccess(level().isClientSide());

        ItemStack heldItem = getItem();

        if (!isTame() && stack.is(WRItems.Tags.ROOST_STALKER_TAMING_ITEMS)) {
            eat(stack);
            if (tame(random.nextDouble() < 0.25, player)) {
                getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
                setHealth(getMaxHealth());
            }

            return COMMON_SUCCESS;
        }

        if (isTame() && isBreedingItem(stack)) {
            if (!level().isClientSide() && canFallInLove() && getAge() == 0) {
                setInLove(player);
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        }

        if (isOwnedBy(player)) {
            if (player.isShiftKeyDown())
            {
                setSit(!isInSittingPose());
                return COMMON_SUCCESS;
            }

            if (stack.isEmpty() && heldItem.isEmpty() && !isLeashed() && player.getPassengers().size() < 3)
            {
                if (!level().isClientSide() && startRiding(player, true))
                {
                    setSit(false);
                    AddPassengerPacket.send(this, player);
                }

                return COMMON_SUCCESS;
            }

            if ((!stack.isEmpty() && !isBreedingItem(stack)) || !heldItem.isEmpty()) {
                setStackInSlot(ITEM_SLOT, stack);
                player.setItemInHand(hand, heldItem);

                return COMMON_SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void doSpecialEffects()
    {
        if (getVariant() == -1 && tickCount % 25 == 0)
        {
            double x = getX() + (Mafs.nextDouble(random) * 0.7d);
            double y = getY() + (random.nextDouble() * 0.5d);
            double z = getZ() + (Mafs.nextDouble(random) * 0.7d);
            level().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05f, 0);
        }
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == ITEM_SLOT) setItem(stack);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot)
    {
        if (slot == EquipmentSlot.MAINHAND) return getItem();
        return super.getItemBySlot(slot);
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
        container.addSlot(new SlotBuilder(getInvHandler(), ITEM_SLOT));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return source == damageSources().drown() || super.isInvulnerableTo(source);
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn)
    {
        return getType().getDimensions().scale(getScale());
    }

    @Override
    public int determineVariant()
    {
        return random.nextDouble() < 0.005? -1 : 0;
    }

    @Override
    // Override normal dragon body controller to allow rotations while sitting: its small enough for it, why not. :P
    protected BodyRotationControl createBodyControl()
    {
        return new BodyRotationControl(this);
    }

    @Override
    public boolean canFly()
    {
        return false;
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
        return WRSounds.ENTITY_STALKER_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_STALKER_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_STALKER_DEATH.value();
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.8f;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFoodItem(ItemStack stack) {
        return stack.is(WRItems.Tags.ROOST_STALKER_FOOD);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(WRItems.Tags.ROOST_STALKER_BREEDING_ITEMS);
    }

    @Override
    public boolean hasGender() {
        return false;
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
    public DragonInvHandler createInv()
    {
        return new DragonInvHandler(this, 1);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractDragonEntity.createDragonAttributes()
                .add(Attributes.STEP_HEIGHT, 0)
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.285)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    class ScavengeGoal extends MoveToBlockGoal
    {
        private Container chest;
        private int searchDelay = 20 + new Random().nextInt(40) + 5;
        private boolean interacted = false;

        public ScavengeGoal(double speed)
        {
            super(RoostStalkerEntity.this, speed, 16);
        }

        @Override
        public boolean canUse()
        {
            boolean flag = !isTame() && !hasItem() && super.canUse() && !isImmobile();
            if (flag) return (chest = getInventoryAtPosition()) != null && !chest.isEmpty();
            else return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            return !hasItem() && chest != null && !isImmobile() && super.canContinueToUse();
        }

        @Override
        public void tick()
        {
            super.tick();

            if (isReachedTarget()) {
                if (hasItem()) return;

                setScavenging(true);

                if (chest == null) return;
                if (chest instanceof ChestBlockEntity chestBE && ChestBlockEntity.getOpenCount(chestBE.getLevel(), chestBE.getBlockPos()) == 0) {
                    if (!interacted) interactChest(chest, true);
                }
                if (!chest.isEmpty() && --searchDelay <= 0)
                {
                    int index = random.nextInt(chest.getContainerSize());
                    ItemStack stack = chest.getItem(index);

                    if (!stack.isEmpty()) {
                        stack = chest.removeItemNoUpdate(index);
                        getInvHandler().insertItem(ITEM_SLOT, stack, false);
                    }
                }
            }
        }

        @Override
        public void stop(){
            super.stop();
            if (interacted) {
                interactChest(chest, false);
            }
            searchDelay = 20 + new Random().nextInt(40) + 5;
            setScavenging(false);
            interacted = false;
        }

        /**
         * Returns the Container (if applicable) of the TileEntity at the specified position
         */
        @Nullable
        public Container getInventoryAtPosition()
        {
            Container inv = null;
            BlockState blockstate = level().getBlockState(blockPos);
            Block block = blockstate.getBlock();
            if (blockstate.hasBlockEntity())
            {
                BlockEntity tileentity = level().getBlockEntity(blockPos);
                if (tileentity instanceof Container container)
                {
                    inv = container;
                    if (inv instanceof ChestBlockEntity && block instanceof ChestBlock)
                        inv = ChestBlock.getContainer((ChestBlock) block, blockstate, level(), blockPos, true);
                }
            }

            return inv;
        }

        /**
         * Return true to set given position as destination
         */
        @Override
        protected boolean isValidTarget(LevelReader world, BlockPos pos)
        {
            return world.getBlockEntity(pos) instanceof Container;
        }

        /**
         * Used to handle the chest opening animation when being used by the scavenger
         */
        private void interactChest(Container intentory, boolean open)
        {
            if (!(intentory instanceof ChestBlockEntity chest)) return; // not a chest, ignore it

            chest.chestLidController.shouldBeOpen(open);
            if (open) incrementOpeners(chest.openersCounter, chest.getLevel(), chest.getBlockPos(), chest.getBlockState());
            else decrementOpeners(chest.openersCounter, chest.getLevel(), chest.getBlockPos(), chest.getBlockState());
            chest.getLevel().blockEvent(chest.getBlockPos(), chest.getBlockState().getBlock(), 1, ChestBlockEntity.getOpenCount(chest.getLevel(),chest.getBlockPos()));
        }

        private void incrementOpeners(ContainerOpenersCounter counter, Level level, BlockPos pos, BlockState state) {
            int i = counter.openCount++;
            if (i == 0) {
                ChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_OPEN);
                level.gameEvent(mob, GameEvent.CONTAINER_OPEN, pos);
            }
            interacted = true;
        }

        private void decrementOpeners(ContainerOpenersCounter counter, Level level, BlockPos pos, BlockState state) {
           counter.openCount--;
            if (counter.openCount == 0) {
                ChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_CLOSE);
                level.gameEvent(mob, GameEvent.CONTAINER_CLOSE, pos);
            }
        }
    }
}
