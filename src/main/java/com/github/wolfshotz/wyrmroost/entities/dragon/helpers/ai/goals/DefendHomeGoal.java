package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.config.WRConfig;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * Basically another target goal that targets things within the home
 */
public class DefendHomeGoal extends TargetGoal
{
    private static final Predicate<LivingEntity> FILTER = e -> e.getType().is(WREntities.Tags.HOME_DEFENDER_ATTACKABLE) && !e.getName().getString().equalsIgnoreCase("Ignore Me");

    private final AbstractDragonEntity defender;
    private final TargetingConditions predicate;

    public DefendHomeGoal(AbstractDragonEntity defender, Predicate<LivingEntity> additionalFilters) {
        super(defender, false, false);
        this.defender = defender;
        this.predicate = TargetingConditions.forCombat().selector(FILTER.and(additionalFilters));
        setFlags(EnumSet.of(Flag.TARGET));
    }

    public DefendHomeGoal(AbstractDragonEntity defender)
    {
        this(defender, e -> true);
    }

    @Override
    public boolean canUse() {
        if (defender.getHealth() <= defender.getMaxHealth() * 0.25) return false;
        if (!defender.getHomePos().isPresent()) return false;
        return defender.getRandom().nextDouble() < 0.2 && (targetMob = findPotentialTarget()) != null && defender.canAttack(targetMob) && !defender.isAlliedTo(targetMob);
    }

    @Override
    public void start() {
        super.start();

        // alert others!
        for (Mob mob : defender.level().getEntitiesOfClass(Mob.class, defender.getBoundingBox().inflate(WRConfig.homeRadius), defender::isAlliedTo))
            mob.setTarget(targetMob);
    }

    @Override
    public boolean canContinueToUse() {
        return defender.isWithinRestriction(targetMob.blockPosition()) && super.canContinueToUse();
    }

    @Override
    protected double getFollowDistance()
    {
        return defender.getRestrictRadius();
    }

    public LivingEntity findPotentialTarget() {
        return defender.level().getNearestEntity(LivingEntity.class,
                predicate,
                defender,
                defender.getX(),
                defender.getY() + defender.getEyeHeight(),
                defender.getZ(),
                new AABB(defender.getRestrictCenter()).inflate(WRConfig.homeRadius));
    }
}
