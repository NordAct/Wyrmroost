package com.github.wolfshotz.wyrmroost.client.render.entity.ldwyrm;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.LDWyrmEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LDWyrmRenderer extends MobRenderer<LDWyrmEntity, LDWyrmModel>
{
    private final ResourceLocation TEXTURE = Wyrmroost.rl("textures/entity/dragon/lesser_desertwyrm/body.png");
    private final ResourceLocation CHRISTMAS = Wyrmroost.rl("textures/entity/dragon/lesser_desertwyrm/christmas.png");

    public LDWyrmRenderer(EntityRendererProvider.Context manager)
    {
        super(manager, new LDWyrmModel(), 0);
    }

    @Override
    public ResourceLocation getTextureLocation(LDWyrmEntity entity)
    {
        return WRConfig.deckTheHalls? CHRISTMAS : TEXTURE;
    }
}
