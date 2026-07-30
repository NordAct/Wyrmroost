package com.github.wolfshotz.wyrmroost.client.render;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.UUID;

public class PlayerShoulderDragonLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public static final HashSet<UUID> RIDING_PLAYER = new HashSet<>();

    public PlayerShoulderDragonLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        for (int i = 0; i < Math.min(player.getPassengers().size(), AbstractDragonEntity.MAX_SHOULDER_DRAGON_PER_PLAYER_COUNT); i++) {
            if (player.getPassengers().get(i) instanceof AbstractDragonEntity dragon) {
                if (!dragon.isInvisible()) tryRenderShoulderDragon(dragon, poseStack, multiBufferSource, light, player, partialTicks, i);
            }
        }
    }

    private void tryRenderShoulderDragon(AbstractDragonEntity dragon, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, AbstractClientPlayer abstractClientPlayer, float partialTicks, int ordinal) {
        RIDING_PLAYER.remove(dragon.getUUID());
        poseStack.pushPose();

        ModelPart anchor = ordinal == 0 ? getParentModel().head : getParentModel().body;

        anchor.translateAndRotate(poseStack);
        float scale = 1 / dragon.getScale();
        float offsetScale = dragon.getScale() / abstractClientPlayer.getScale();
        Vec3 ridingOffset = dragon.getRidingPosOffset(ordinal);
        poseStack.translate(0, -0.2960000524520874 * ((ordinal == 0 ? 0.6 : 0) + offsetScale) - 0.5 * (1 - offsetScale), 0);
        poseStack.translate(ridingOffset.x,  ordinal == 0 ? 0 : 0.3, ridingOffset.z);
        poseStack.scale(-scale, -scale, scale);

        RenderHelper.renderEntity(dragon, partialTicks, poseStack, multiBufferSource, light);

        poseStack.popPose();
        RIDING_PLAYER.add(dragon.getUUID());
    }
}
