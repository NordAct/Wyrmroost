package com.github.wolfshotz.wyrmroost.client.render.entity;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.model.WREntityModel;
import com.github.wolfshotz.wyrmroost.client.render.PlayerShoulderDragonLayer;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractDragonRenderer<T extends AbstractDragonEntity, M extends WREntityModel<T>> extends MobRenderer<T, M>
{
    public static final String BASE_PATH = "textures/entity/dragon/";

    public AbstractDragonRenderer(EntityRendererProvider.Context manager, M model, float shadowSize) {
        super(manager, model, shadowSize);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.getVehicle() instanceof Player player) {
            if (PlayerShoulderDragonLayer.RIDING_PLAYER.contains(entity.getUUID())) return;
            else if (Minecraft.getInstance().player == player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void setupRotations(T entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        if (entity.getVehicle() instanceof Player) {
            if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
                poseStack.translate(0, (entity.getBbHeight() + 0.1f) / scale, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
        } else super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
    }

    /**
     * A conditional layer that can only render if certain conditions are met.
     * E.G. is the dragon sleeping, saddled, etc
     */
    public class ConditionalLayer extends RenderLayer<T, M>
    {
        public Predicate<T> conditions;
        public final Function<T, RenderType> type;

        public ConditionalLayer(Predicate<T> conditions, Function<T, RenderType> type)
        {
            super(AbstractDragonRenderer.this);
            this.conditions = conditions;
            this.type = type;
        }

        @Override
        public void render(PoseStack ms, MultiBufferSource buffer, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            if (conditions.test(entity))
                renderLayer(ms, buffer, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        public void renderLayer(PoseStack ms, MultiBufferSource buffer, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            VertexConsumer builder = buffer.getBuffer(type.apply(entity));
            getModel().renderToBuffer(ms, builder, packedLightIn, getOverlayCoords(entity, 0.0F));
        }

        public ConditionalLayer addCondition(Predicate<T> condition)
        {
            this.conditions = conditions.and(condition);
            return this;
        }
    }

    public class GlowLayer extends ConditionalLayer
    {
        public final Function<T, ResourceLocation> texture;

        public GlowLayer(Function<T, ResourceLocation> texture)
        {
            super(e -> true, null);
            this.texture = texture;
        }

        @Override
        public void render(PoseStack ms, MultiBufferSource buffer, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            if (conditions.test(entity)) {
                ResourceLocation rl = texture.apply(entity);
                if (rl != null) {
                    VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentEmissive(rl));
                    getModel().renderToBuffer(ms, builder, 15728640, OverlayTexture.NO_OVERLAY);
                }
            }
        }
    }

    public class ArmorLayer extends RenderLayer<T, M>
    {
        private final int slotIndex;

        public ArmorLayer(int slotIndex)
        {
            super(AbstractDragonRenderer.this);
            this.slotIndex = slotIndex;
        }

        @Override
        public void render(PoseStack ms, MultiBufferSource type, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            if (entity.hasArmor())
            {
                VertexConsumer builder = type.getBuffer(RenderType.entityCutoutNoCull(getArmorTexture(entity)));
                getModel().renderToBuffer(ms, builder, packedLightIn, OverlayTexture.NO_OVERLAY);
            }
        }

        public ResourceLocation getArmorTexture(T entity)
        {
            String path = entity.getArmor().getItem().builtInRegistryHolder().getKey().location().getPath().replace("_dragon_armor", "");
            return Wyrmroost.rl(String.format("%s%s/accessories/armor_%s.png", BASE_PATH, EntityType.getKey(entity.getType()).getPath(), path));
        }
    }
}