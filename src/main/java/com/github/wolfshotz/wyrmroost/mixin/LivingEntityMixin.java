package com.github.wolfshotz.wyrmroost.mixin;

import com.github.wolfshotz.wyrmroost.client.render.RenderHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void addStaffGlow(CallbackInfoReturnable<Boolean> cir) {
        if (RenderHelper.ENTITY_OUTLINE_MAP.containsKey(this)) cir.setReturnValue(true);
    }
}
