package com.github.wolfshotz.wyrmroost.client.render;

import com.github.wolfshotz.wyrmroost.client.render.entity.dragon_egg.DragonEggRenderer;
import com.github.wolfshotz.wyrmroost.registry.WRDataComponentTypes;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DragonEggStackRenderer extends BlockEntityWithoutLevelRenderer
{
    public DragonEggStackRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transform, PoseStack ms, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        VertexConsumer builder = ItemRenderer.getFoilBuffer(buffer, DragonEggRenderer.MODEL.renderType(getEggTexture(stack)), false, stack.hasFoil());
        DragonEggRenderer.MODEL.renderToBuffer(ms, builder, combinedLight, combinedOverlay);
    }

    private ResourceLocation getEggTexture(ItemStack stack)
    {
        if (stack.has(WRDataComponentTypes.DRAGON_TYPE_COMPONENT))
        {
            EntityType<?> type = ModUtils.getEntityTypeByKey(stack.get(WRDataComponentTypes.DRAGON_TYPE_COMPONENT));
            if (type != null) return DragonEggRenderer.getDragonEggTexture(type);
        }

        return DragonEggRenderer.DEFAULT_TEXTURE;
    }
}
