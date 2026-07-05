package com.github.wolfshotz.wyrmroost.mixin;

import com.github.wolfshotz.wyrmroost.client.render.RenderHelper;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void addStaffGlow(CallbackInfoReturnable<Boolean> cir) {
        if (RenderHelper.ENTITY_OUTLINE_MAP.containsKey(this)) cir.setReturnValue(true);
    }

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void addStaffGlowColor(CallbackInfoReturnable<Integer> cir) {
        if (RenderHelper.ENTITY_OUTLINE_MAP.containsKey(this)) cir.setReturnValue(RenderHelper.ENTITY_OUTLINE_MAP.getInt(this));
    }
}
