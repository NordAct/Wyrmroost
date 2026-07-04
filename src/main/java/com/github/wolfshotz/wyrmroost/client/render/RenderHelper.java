package com.github.wolfshotz.wyrmroost.client.render;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.items.staff.DragonStaffItem;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

public class RenderHelper
{
    // == [Render Types] ==
    public static RenderType getAdditiveGlow(ResourceLocation locationIn) {
        return Util.<ResourceLocation, RenderType>memoize((texture) -> RenderType.create("glow_additive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(RenderType.ADDITIVE_TRANSPARENCY)
                .createCompositeState(false)
        )).apply(locationIn);
    }

    public static RenderType getTranslucentGlow(ResourceLocation locationIn)
    {
        return Util.<ResourceLocation, RenderType>memoize((texture) -> RenderType.create("glow_transluscent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setCullState(RenderType.NO_CULL)
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .createCompositeState(false))).apply(locationIn);
    }

    public static RenderType getThiccLines(double thickness)
    {
        return Util.<Double, RenderType>memoize((thick) -> RenderType.create("thickened_lines", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINE_STRIP, 1536, RenderType.CompositeState.builder()
                .setLineState(new  RenderStateShard.LineStateShard(OptionalDouble.of(thick)))
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .createCompositeState(false))).apply(thickness);
    }

    // == [Rendering] ==

    @SubscribeEvent
    public static void renderWorld(RenderLevelStageEvent evt)
    {
        PoseStack ms = evt.getPoseStack();
        float partialTicks = evt.getPartialTick().getGameTimeDeltaPartialTick(false);

        renderDragonStaff(ms, partialTicks);
        DebugBox.INSTANCE.render(ms);
    }

    private static final Object2IntMap<Entity> ENTITY_OUTLINE_MAP = new Object2IntOpenHashMap<>(1);

    public static void renderEntityOutline(Entity entity, int red, int green, int blue, int alpha)
    {
        //ENTITY_OUTLINE_MAP.put(entity, ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF)));
    }

    // todo: find a better, shaders friendly way to do this
    public static void renderEntities(RenderLivingEvent.Pre<? super LivingEntity, ?> event)
    {
        //LivingEntity entity = event.getEntity(); //todo check if it even needs this anymore - Nord
        //int color = ENTITY_OUTLINE_MAP.removeInt(entity);
        //if (color != 0)
        //{
        //    event.setCanceled(true);
//
        //    Minecraft mc = ClientEvents.getClient();
        //    OutlineLayerBuffer buffer = mc.getRenderTypeBuffers().getOutlineBufferSource();
        //    MatrixStack ms = event.getMatrixStack();
        //    LivingRenderer<? super LivingEntity, ?> renderer = event.getRenderer();
        //    float partialTicks = event.getPartialRenderTick();
        //    float yaw = Mth.lerpInt(partialTicks, entity.prevRotationYaw, entity.rotationYaw);
//
        //    buffer.setColor((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF);
        //    renderer.render(entity, yaw, partialTicks, ms, buffer, 15728640);
        //    buffer.finish();
        //}
    }

//    public static void fogColors(EntityViewRenderEvent.FogColors evt)
//    {
//        EffectInstance effect = ClientEvents.getPlayer().getActivePotionEffect(WREffects.SILK.get());
//        if (effect != null)
//        {
//            evt.setBlue(evt.getRed() + 0.875f);
//            evt.setGreen(evt.getGreen() + 0.875f);
//            evt.setRed(evt.getBlue() + 0.875f);
//        }
//    }
//
//    public static void renderFog(EntityViewRenderEvent.RenderFogEvent evt)
//    {
//        EffectInstance effect = ClientEvents.getPlayer().getActivePotionEffect(WREffects.SILK.get());
//        if (effect != null)
//        {
//            float duration = (float) effect.getDuration();
//            float lerp = MathHelper.lerp(Math.min(1f, duration / 10f), evt.getFarPlaneDistance(), 5f);
//            RenderSystem.fogStart(lerp * 0.25f);
//            RenderSystem.fogEnd(lerp);
//        }
//    }

    private static void renderDragonStaff(PoseStack ms, float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ItemStack stack = ModUtils.getHeldStack(player, WRItems.DRAGON_STAFF.value());
        if (stack == null) return;
        AbstractDragonEntity dragon = DragonStaffItem.getBoundDragon(mc.level, stack);
        if (dragon == null) return;

        DragonStaffItem.getAction(stack).render(dragon, ms, partialTicks);
        if (WRConfig.renderEntityOutlines)
        {
            renderEntityOutline(dragon, 0, 255, 255, (int) (Mth.cos((dragon.tickCount + partialTicks) * 0.2f) * 35 + 45));
            LivingEntity target = dragon.getTarget();
            if (target != null) renderEntityOutline(target, 255, 0, 0, 100);
        }
        dragon.getHomePos().ifPresent(pos -> RenderHelper.drawBlockPos(ms, pos, dragon.level(), 4, 0xff0000ff));
    }

    public static void drawShape(PoseStack ms, VertexConsumer buffer, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha)
    {
        Matrix4f matrix4f = ms.last().pose();
        shapeIn.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
        {
            buffer.addVertex(matrix4f, (float) (x1 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).setColor(red, green, blue, alpha);
            buffer.addVertex(matrix4f, (float) (x2 + xIn), (float) (y2 + yIn), (float) (z2 + zIn)).setColor(red, green, blue, alpha);
        });
    }

    public static void drawBlockPos(PoseStack ms, BlockPos pos, Level world, double lineThickness, int argb)
    {
        Vec3 view = ClientEvents.getProjectedView();
        double x = pos.getX() - view.x;
        double y = pos.getY() - view.y;
        double z = pos.getZ() - view.z;

        MultiBufferSource.BufferSource impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderHelper.drawShape(ms,
                impl.getBuffer(getThiccLines(lineThickness)),
                world.getBlockState(pos).getShape(world, pos),
                x, y, z,
                ((argb >> 16) & 0xFF) / 255f, ((argb >> 8) & 0xFF) / 255f, (argb & 0xFF) / 255f, ((argb >> 24) & 0xFF) / 255f);
    }

    public enum DebugBox
    {
        INSTANCE;

        private int time = 0;
        private AABB aabb = null;
        private int color = 0xff0000ff;

        public DebugBox queue(AABB aabb)
        {
            return queue(aabb, Integer.MAX_VALUE);
        }

        public DebugBox queue(AABB aabb, int time)
        {
            this.aabb = aabb;
            this.time = time;
            return this;
        }

        public void setColor(int color)
        {
            this.color = color;
        }

        public void reset()
        {
            this.aabb = null;
            this.time = 0;
            this.color = 0xff0000ff;
        }

        public void render(PoseStack ms)
        {
            if (!WRConfig.debugMode) return;
            if (aabb == null) return;

            Vec3 view = ClientEvents.getProjectedView();
            double x = view.x;
            double y = view.y;
            double z = view.z;

            MultiBufferSource.BufferSource type = Minecraft.getInstance().renderBuffers().bufferSource();
            LevelRenderer.renderLineBox(
                    ms, type.getBuffer(RenderType.lines()),
                    aabb.minX - x,
                    aabb.minY - y,
                    aabb.minZ - z,
                    aabb.maxX - x,
                    aabb.maxY - y,
                    aabb.maxZ - z,
                    (color & 0xff) / 255f,
                    ((color >> 8) & 0xff) / 255f,
                    ((color >> 16) & 0xff) / 255f,
                    ((color >> 24) & 0xff) / 255f);

            if (--time <= 0) aabb = null;
        }
    }
}
