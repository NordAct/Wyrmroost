package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Predicate;

public class WRAvoidEntityGoal<T extends LivingEntity> extends Goal
{
    private final AbstractDragonEntity entity;
    private final Class<T> classToAvoid;
    private final float avoidDistance;
    private final double farSpeed;
    private final double nearSpeed;
    private final TargetingConditions builtTargetSelector;
    private T avoidTarget;

    public WRAvoidEntityGoal(AbstractDragonEntity entity, Class<T> classToAvoid, float avoidDistance, double speed)
    {
        this(entity, classToAvoid, e -> true, avoidDistance, speed, speed * 1.43);
    }

    public WRAvoidEntityGoal(AbstractDragonEntity entityIn, Class<T> avoidClass, Predicate<LivingEntity> targetPredicate, float distance, double nearSpeedIn, double farSpeedIn) {
        this.entity = entityIn;
        this.classToAvoid = avoidClass;
        this.avoidDistance = distance;
        this.farSpeed = nearSpeedIn;
        this.nearSpeed = farSpeedIn;
        this.builtTargetSelector = TargetingConditions.forCombat().range(distance).selector(targetPredicate);

        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        if (entity.isTame()) return false;
        this.avoidTarget = entity.level().getNearestEntity(classToAvoid, builtTargetSelector, entity, entity.getX(), entity.getY(), entity.getZ(), entity.getBoundingBox().inflate(avoidDistance, 3.0D, avoidDistance));
        if (avoidTarget == null) return false;
        Vec3 pos =  DefaultRandomPos.getPosAway(entity, 16, 7, avoidTarget.position());
        if (pos == null) return false;
        if (avoidTarget.position().distanceToSqr(pos) < avoidTarget.distanceToSqr(entity)) return false;
        return entity.getNavigation().moveTo(pos.x, pos.y, pos.z, farSpeed);
    }

    @Override
    public boolean canContinueToUse()
    {
        return !entity.getNavigation().isDone();
    }


    public void tick()
    {
        if (entity.distanceToSqr(avoidTarget) < 49) entity.getNavigation().setSpeedModifier(nearSpeed);
        else entity.getNavigation().setSpeedModifier(farSpeed);
    }

    @Override
    public void stop()
    {
        avoidTarget = null;
    }
}
