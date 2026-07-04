package com.github.wolfshotz.wyrmroost.client.render.entity.alpine;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.render.entity.AbstractDragonRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.AlpineEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AlpineRenderer extends AbstractDragonRenderer<AlpineEntity, AlpineModel>
{
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[6];

    public AlpineRenderer(EntityRendererProvider.Context manager) { super(manager, new AlpineModel(), 2f); }

    @Override
    public ResourceLocation getTextureLocation(AlpineEntity entity)
    {
        int variant = Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1);
        if (TEXTURES[variant] == null)
        {
            String path = BASE_PATH + "alpine/body_" + variant;
            if (WRConfig.deckTheHalls) path += "_christmas";
            return TEXTURES[variant] = Wyrmroost.rl(path + ".png");
        }
        return TEXTURES[variant];
    }
}
