package com.github.wolfshotz.wyrmroost.client.render;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.items.staff.DragonStaffItem;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

@EventBusSubscriber
public class RenderHelper
{
    // == [Rendering] ==
    @SubscribeEvent
    public static void renderWorld(RenderLevelStageEvent evt)
    {
        if (evt.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            PoseStack ms = evt.getPoseStack();
            ms.pushPose();
            float partialTicks = evt.getPartialTick().getGameTimeDeltaPartialTick(false);
            renderDragonStaff(ms, partialTicks);
            DebugBox.INSTANCE.render(ms);
            ms.popPose();
        }
    }

    public static final Object2IntMap<Entity> ENTITY_OUTLINE_MAP = new Object2IntOpenHashMap<>(1);

    public static void renderEntityOutline(Entity entity, int red, int green, int blue, int alpha)
    {
        ENTITY_OUTLINE_MAP.put(entity, ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF)));
    }

    @SubscribeEvent
    public static void renderEntities(RenderLivingEvent.Post<? super LivingEntity, ?> event)
    {
        LivingEntity entity = event.getEntity();
        ENTITY_OUTLINE_MAP.removeInt(entity);
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
