package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

/**
 * Created by com.github.WolfShotz - 8/26/19 - 16:12
 * <p>
 * Disallows rotations while sitting, sleeping, and helps control yaw while controlling
 */
public class DragonBodyController extends BodyRotationControl
{
    public AbstractDragonEntity dragon;

    public DragonBodyController(AbstractDragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
    }

    @Override
    public void clientTick()
    {
        // No body rotations while sitting or sleeping
        if (dragon.isSleeping()) return;

        // Clamp the head rotation to 70 degrees while sitting
        if (dragon.isInSittingPose())
        {
            clampHeadRotation(70f);
            return;
        }

        // clamp head to 120 degrees, rotate body according to head
        if (dragon.hasControllingPassenger() || dragon.isFlying()) {
            clampHeadRotation(120f);
            dragon.setYRot(dragon.yBodyRot = Mth.wrapDegrees(Mth.rotateIfNecessary(dragon.getYHeadRot(), dragon.yBodyRot, dragon.getYawRotationSpeed())));
            return;
        }

        super.clientTick();
    }

    public void clampHeadRotation(float clampDeg)
    {
        dragon.yHeadRot = Mth.rotateIfNecessary(dragon.getYHeadRot(), dragon.yBodyRot, clampDeg);
    }
}
