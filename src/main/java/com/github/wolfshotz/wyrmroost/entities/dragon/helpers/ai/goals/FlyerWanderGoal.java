package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlyerWanderGoal extends WaterAvoidingRandomStrollGoal
{
    private final AbstractDragonEntity dragon;

    public FlyerWanderGoal(AbstractDragonEntity dragon, double speed, float probability)
    {
        super(dragon, speed, probability);
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));

        this.dragon = dragon;
    }

    public FlyerWanderGoal(AbstractDragonEntity dragon, double speed)
    {
        this(dragon, speed, 0.001f);
    }

    @Override
    public boolean canUse()
    {
        if (dragon.isInSittingPose()) return false;
        if (dragon.isControlledByLocalInstance()) return false;
        Vec3 vec3d;
        if (dragon.isFlying() && (vec3d = getPosition()) != null)
        {
            this.wantedX = vec3d.x;
            this.wantedY = vec3d.y;
            this.wantedZ = vec3d.z;
            this.forceTrigger = false;
            return true;
        }

        return super.canUse();
    }

    @Override
    public Vec3 getPosition()
    {
        Vec3 position = null;

        if (dragon.isFlying() || (!dragon.isLeashed() && dragon.getRandom().nextFloat() <= probability + 0.02))
        {
            if ((dragon.maySleep() && !dragon.level().isDay()) || dragon.getRandom().nextFloat() <= probability)
                position = LandRandomPos.getPos(dragon, 20, 25);
            else
            {
                Vec3 vec3d = dragon.getLookAngle();
                if (!dragon.isWithinRestriction())
                    vec3d = dragon.getRestrictCenter().getCenter().subtract(dragon.position()).normalize();

                int yOffset = dragon.getAltitude() > 40? 10 : 0;
                position = AirRandomPos.getPosTowards(dragon, 50, 30, yOffset, vec3d, 10);
            }
            if (position != null && position.y > dragon.getY() + dragon.getBbHeight() && !dragon.isFlying()) dragon.setFlying(true);
        }

        return position == null? super.getPosition() : position;
    }
}
