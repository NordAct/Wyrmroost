package com.github.wolfshotz.wyrmroost.entities.projectile.breath;

import com.github.wolfshotz.wyrmroost.config.WRConfig;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.DragonProjectileEntity;
import com.github.wolfshotz.wyrmroost.registry.WRAttributes;
import com.github.wolfshotz.wyrmroost.registry.WRDamageTypes;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class FireBreathEntity extends BreathWeaponEntity
{
    public FireBreathEntity(EntityType<? extends DragonProjectileEntity> type, Level world)
    {
        super(type, world);
    }

    public FireBreathEntity(AbstractDragonEntity shooter) {
        super((EntityType<? extends DragonProjectileEntity>)WREntities.FIRE_BREATH.value(), shooter);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (isInWater())
        {
            if (getRandom().nextDouble() <= 0.25d) playSound(SoundEvents.FIRE_EXTINGUISH, 1, 1);
            for (int i = 0; i < 15; i++)
                level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), Mafs.nextDouble(getRandom()) * 0.2f, getRandom().nextDouble() * 0.08f, Mafs.nextDouble(getRandom()) * 0.2f);
            discard();
            return;
        }

        Vec3 motion = getDeltaMovement();
        double x = getX() + motion.x + (getRandom().nextGaussian() * 0.2);
        double y = getY() + motion.y + (getRandom().nextGaussian() * 0.2) + 0.5d;
        double z = getZ() + motion.z + (getRandom().nextGaussian() * 0.2);
        level().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
    }

    @Override
    public void onBlockImpact(BlockPos pos, Direction direction)
    {
        super.onBlockImpact(pos, direction);
        if (level().isClientSide()) return;

        BlockState state = level().getBlockState(pos);
        if (CampfireBlock.canLight(state))
        {
            level().setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 11);
            return;
        }

        double flammability = WRConfig.fireBreathFlammability;
        if (level().getGameRules().getBoolean(GameRules.RULE_DOFIRETICK) && WRConfig.canGrief(level()) && flammability != 0) // respect game rules
        {
            BlockPos offset = pos.relative(direction);

            if (level().getBlockState(offset).isAir() && (flammability == 1 || getRandom().nextDouble() <= flammability))
                level().setBlock(offset, BaseFireBlock.getState(level(), offset), 11);
        }
    }

    @Override
    public void onEntityImpact(Entity entity)
    {
        if (level().isClientSide()) return;

        float damage = (float) shooter.getAttributeValue(WRAttributes.PROJECTILE_DAMAGE);
        if (level().isRainingAt(entity.blockPosition())) damage *= 0.75f;

        if (entity.fireImmune()) damage *= 0.25; // impact damage
        else entity.igniteForSeconds(8);

        entity.hurt(getDamageSource(getRandom().nextDouble() > 0.2? WRDamageTypes.FIRE_BREATH_0 : WRDamageTypes.FIRE_BREATH_1), damage);
    }

    @Override // Because we do it better.
    public boolean displayFireAnimation()
    {
        return false;
    }

    @Override
    public boolean isOnFire()
    {
        return true;
    }
}
