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
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[12];

    public AlpineRenderer(EntityRendererProvider.Context manager) { super(manager, new AlpineModel(), 2f); }

    @Override
    public ResourceLocation getTextureLocation(AlpineEntity entity)
    {
        int variant;
        if (WRConfig.deckTheHalls) {
            variant = Mth.clamp(entity.getVariant(), 0, (TEXTURES.length - 1) / 2);
            if (TEXTURES[variant] == null) {
                String path = BASE_PATH + "alpine/body_" + variant + "_christmas.png";
                TEXTURES[variant] = Wyrmroost.rl(path);
            }
        } else {
            variant = Mth.clamp((entity.isMale() ? 0 : 6) + entity.getVariant(), 0, TEXTURES.length - 1);
            if (TEXTURES[variant] == null) {
                String path = BASE_PATH + "alpine/body_" + entity.getVariant() + (entity.isMale()? "m" : "f") + ".png";
                TEXTURES[variant] = Wyrmroost.rl(path);
            }
        }
        return TEXTURES[variant];
    }
}
