package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;

public class DragonBreedGoal extends Goal
{
    protected final AbstractDragonEntity dragon;
    protected AbstractDragonEntity targetMate;
    protected int spawnBabyDelay;

    public DragonBreedGoal(AbstractDragonEntity dragon)
    {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.dragon = dragon;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    @Override
    public boolean canUse()
    {
        if (!dragon.isInLove()) return false;
        final int breedLimit = WRConfig.breedLimits.getOrDefault(EntityType.getKey(dragon.getType()).getPath(), 0);
        if (breedLimit > 0 && dragon.breedCount >= breedLimit)
        {
            dragon.resetLove();
            return false;
        }
        return (targetMate = getNearbyMate()) != null;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean canContinueToUse()
    {
        return targetMate.isAlive() && targetMate.isInLove() && dragon.isInLove() && spawnBabyDelay < 60;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void stop()
    {
        targetMate = null;
        spawnBabyDelay = 0;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void tick()
    {
        dragon.getLookControl().setLookAt(targetMate, 10f, dragon.getHeadRotSpeed());
        dragon.getNavigation().moveTo(targetMate, 1);
        if (++spawnBabyDelay >= 60 && dragon.distanceTo(targetMate) < dragon.getBbWidth() * 2)
            dragon.spawnChildFromBreeding((ServerLevel) dragon.level(), targetMate);
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    @Nullable
    protected AbstractDragonEntity getNearbyMate()
    {
        return dragon.level()
                .getEntitiesOfClass(dragon.getClass(), dragon.getBoundingBox().inflate(dragon.getBbWidth() * 8), e -> e != dragon && e instanceof AbstractDragonEntity abstractDragonEntity && abstractDragonEntity.canMate(dragon))
                .stream()
                .min(Comparator.comparingDouble(dragon::distanceToSqr)).orElse(null);
    }
}
