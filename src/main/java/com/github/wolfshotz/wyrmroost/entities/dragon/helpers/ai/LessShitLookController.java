package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class LessShitLookController extends LookControl
{
    private boolean frozen;
    private boolean restore;

    public LessShitLookController(Mob entity)
    {
        super(entity);
    }

    public void tick()
    {
        if (restore)
        {
            this.restore = false;
            mob.yHeadRot = rotateTowards(mob.yHeadRot, mob.yBodyRot, mob.getMaxHeadYRot());
            mob.setXRot(rotateTowards(mob.getXRot(), 0, mob.getMaxHeadXRot()));
            return;
        }

        if (frozen)
        {
            frozen = false;
            return;
        }

        mob.setXRot(0);
        if (isLookingAtTarget()) {
            lookAtCooldown = 0;
            mob.yHeadRot = rotateTowards(mob.yHeadRot, getYRotD().orElse(0f), yMaxRotSpeed);
            mob.setXRot(rotateTowards(mob.getXRot(), getXRotD().orElse(0f), yMaxRotSpeed));
        }
        else mob.yHeadRot = rotateTowards(mob.yHeadRot, mob.yBodyRot, yMaxRotSpeed);

        if (!mob.getNavigation().isDone())
            mob.yHeadRot = Mth.rotateIfNecessary(mob.yHeadRot, mob.yBodyRot, yMaxRotSpeed);
    }

    protected boolean resetXRotOnTick() { return !frozen; }

    public void freeze()
    {
        this.frozen = true;
        lookAtCooldown = 0;
    }

    public void restore()
    {
        this.restore = true;
        this.frozen = true;
        lookAtCooldown = 0;
    }
}
