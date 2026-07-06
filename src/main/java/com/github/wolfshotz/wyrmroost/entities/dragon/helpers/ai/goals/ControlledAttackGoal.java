package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import java.util.function.Consumer;

public class ControlledAttackGoal extends MeleeAttackGoal
{
    private final AbstractDragonEntity dragon;
    private final Consumer<AbstractDragonEntity> attack;

    public ControlledAttackGoal(AbstractDragonEntity dragon, double speed, boolean longMemory, Consumer<AbstractDragonEntity> attack)
    {
        super(dragon, speed, longMemory);
        this.attack = attack;
        this.dragon = dragon;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !dragon.hasControllingPassenger();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = dragon.getTarget();
        if (target == null) return false;
        return !dragon.hasControllingPassenger() && dragon.wantsToAttack(target, dragon.getOwner()) && super.canContinueToUse();
    }

    @Override
    public void start() {
        mob.setAggressive(true);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy) {
        if (canPerformAttack(enemy) && enemy.getVehicle() != dragon && dragon.noActiveAnimation()) {
            attack.accept(dragon);
            resetAttackCooldown();
        }
    }
}
