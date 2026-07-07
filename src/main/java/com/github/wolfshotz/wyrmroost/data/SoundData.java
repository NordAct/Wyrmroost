package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft
        .sounds.SoundEvent;
import net.minecraft
        .sounds
        .SoundSource;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

// you know this was initially made by someone who knows only english, because such irresponsible approach to making subtitles would drive mad any translator - Nord
public class SoundData implements DataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<SoundEvent> REGISTERED = new HashSet<>();

    private final Builder soundBuilder = new Builder();
    private final ExistingFileHelper existingFileHelper;
    private final PackOutput output;

    public SoundData(PackOutput output, ExistingFileHelper existingFileHelper)
    {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        JsonObject json = new JsonObject();
        registerSounds(json);
        for (SoundEvent value : WRSounds.REGISTRY.getEntries().stream().map(DeferredHolder::value).toList())
            if (!REGISTERED.contains(value))
                throw new IllegalArgumentException("Unregistered Sound event: " + value.getLocation());
        return DataProvider.saveStable(cache, json, output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve( Wyrmroost.MOD_ID + "/sounds.json"));
    }

    public void registerSounds(JsonObject json)
    {
        getBuilder(WRSounds.WING_FLAP.value())
                .subtitle("sound.wyrmroost.entity.other.flap")
                .sound(Wyrmroost.rl("entity/other/wings/flap1"), 3, 1)
                .sound(Wyrmroost.rl("entity/other/wings/flap2"), 3, 1)
                .sound(Wyrmroost.rl("entity/other/wings/flap3"), 3, 1)
                .build(json);
        getBuilder(WRSounds.FIRE_BREATH.value())
                .subtitle("sound.wyrmroost.entity.other.fire_breath")
                .sound(Wyrmroost.rl("entity/other/breath/fire_breath"))
                .build(json);

        getBuilder(WRSounds.ENTITY_LDWYRM_IDLE.value())
                .subtitle("sound.wyrmroost.entity.lesser_desertwyrm.idle")
                .sounds(Wyrmroost::rl, "entity/lesser_desertwyrm/%s", "idle1", "idle2")
                .build(json);

        getBuilder(WRSounds.ENTITY_SILVERGLIDER_IDLE.value())
                .subtitle("sound.wyrmroost.entity.silver_glider.idle")
                .sounds(Wyrmroost::rl, "entity/silver_glider/%s", "idle1", "idle2", "idle3", "idle4")
                .build(json);
        getBuilder(WRSounds.ENTITY_SILVERGLIDER_HURT.value())
                .subtitle("sound.wyrmroost.entity.silver_glider.hurt").sound(Wyrmroost.rl("entity/silver_glider/hurt"))
                .build(json);
        getBuilder(WRSounds.ENTITY_SILVERGLIDER_DEATH.value())
                .subtitle("sound.wyrmroost.entity.silver_glider.death").sound(Wyrmroost.rl("entity/silver_glider/death"))
                .build(json);

        getBuilder(WRSounds.ENTITY_OWDRAKE_IDLE.value())
                .subtitle("sound.wyrmroost.entity.overworld_drake.idle")
                .sounds(Wyrmroost::rl, "entity/overworld_drake/%s", "idle1", "idle2", "idle3")
                .build(json);
        getBuilder(WRSounds.ENTITY_OWDRAKE_HURT.value())
                .subtitle("sound.wyrmroost.entity.overworld_drake.hurt")
                .sounds(Wyrmroost::rl, "entity/overworld_drake/%s", "idle1", "idle2", "idle3")
                .build(json);
        getBuilder(WRSounds.ENTITY_OWDRAKE_DEATH.value())
                .subtitle("sound.wyrmroost.entity.overworld_drake.death").sound(Wyrmroost.rl("entity/overworld_drake/death"))
                .build(json);
        getBuilder(WRSounds.ENTITY_OWDRAKE_ROAR.value())
                .subtitle("sound.wyrmroost.entity.overworld_drake.roar").sound(Wyrmroost.rl("entity/overworld_drake/roar"))
                .build(json);

        getBuilder(WRSounds.ENTITY_STALKER_IDLE.value())
                .subtitle("Rooststalker clicks")
                .sounds(Wyrmroost::rl, "entity/roost_stalker/%s", "idle1", "idle2", "idle3")
                .build(json);
        getBuilder(WRSounds.ENTITY_STALKER_HURT.value())
                .subtitle("Rooststalker screeches").sound(Wyrmroost.rl("entity/roost_stalker/hurt"))
                .build(json);
        getBuilder(WRSounds.ENTITY_STALKER_DEATH.value())
                .subtitle("Rooststalker moans").sound(Wyrmroost.rl("entity/roost_stalker/death"))
                .build(json);

        getBuilder(WRSounds.ENTITY_BFLY_IDLE.value())
                .subtitle("sound.wyrmroost.entity.butterfly_leviathan.idle")
                .sounds(Wyrmroost::rl, "entity/butterfly_leviathan/%s", "idle1", "idle2", "idle3")
                .build(json);
        getBuilder(WRSounds.ENTITY_BFLY_HURT.value())
                .subtitle("sound.wyrmroost.entity.butterfly_leviathan.hurt")
                .sounds(Wyrmroost::rl, "entity/butterfly_leviathan/%s", "hurt1", "hurt2")
                .build(json);
        getBuilder(WRSounds.ENTITY_BFLY_ROAR.value())
                .subtitle("sound.wyrmroost.entity.butterfly_leviathan.roar").sound(Wyrmroost.rl("entity/butterfly_leviathan/roar"))
                .build(json);
        getBuilder(WRSounds.ENTITY_BFLY_DEATH.value())
                .subtitle("sound.wyrmroost.entity.butterfly_leviathan.death").sound(Wyrmroost.rl("entity/butterfly_leviathan/death"))
                .build(json);

        getBuilder(WRSounds.ENTITY_CANARI_IDLE.value())
                .subtitle("sound.wyrmroost.entity.canari_wyvern.idle")
                .sounds(Wyrmroost::rl, "entity/canari_wyvern/%s", "idle1", "idle2", "idle3", "idle4")
                .build(json);
        getBuilder(WRSounds.ENTITY_CANARI_HURT.value())
                .subtitle("sound.wyrmroost.entity.canari_wyvern.hurt")
                .sounds(Wyrmroost::rl, "entity/canari_wyvern/%s", "hurt1", "hurt2", "hurt3")
                .build(json);
        getBuilder(WRSounds.ENTITY_CANARI_DEATH.value())
                .subtitle("sound.wyrmroost.entity.canari_wyvern.death").sound(Wyrmroost.rl("entity/canari_wyvern/death"))
                .build(json);

        getBuilder(WRSounds.ENTITY_DFD_IDLE.value())
                .subtitle("sound.wyrmroost.entity.dragonfruit_drake.idle")
                .sounds(Wyrmroost::rl, "entity/dragonfruit_drake/%s", "idle1", "idle2", "idle3", "idle4")
                .build(json);
        getBuilder(WRSounds.ENTITY_DFD_HURT.value())
                .subtitle("sound.wyrmroost.entity.dragonfruit_drake.hurt")
                .sounds(Wyrmroost::rl, "entity/dragonfruit_drake/%s", "hurt", "hurt1", "hurt2", "hurt3")
                .build(json);
        getBuilder(WRSounds.ENTITY_DFD_DEATH.value())
                .subtitle("sound.wyrmroost.entity.dragonfruit_drake.death").sound(Wyrmroost.rl("entity/dragonfruit_drake/death"))
                .build(json);

        getBuilder(WRSounds.ENTITY_ROYALRED_IDLE.value())
                .subtitle("sound.wyrmroost.entity.royal_red.idle")
                .sounds(Wyrmroost::rl, "entity/royal_red/%s", "idle1", "idle2")
                .build(json);
        getBuilder(WRSounds.ENTITY_ROYALRED_HURT.value())
                .subtitle("sound.wyrmroost.entity.royal_red.hurt")
                .sounds(Wyrmroost::rl, "entity/royal_red/%s", "hurt1", "hurt2")
                .build(json);
        getBuilder(WRSounds.ENTITY_ROYALRED_DEATH.value())
                .subtitle("sound.wyrmroost.entity.royal_red.death").sound(Wyrmroost.rl("entity/royal_red/death"))
                .build(json);
        getBuilder(WRSounds.ENTITY_ROYALRED_ROAR.value())
                .subtitle("sound.wyrmroost.entity.royal_red.roar").sound(Wyrmroost.rl("entity/royal_red/roar"))
                .build(json);

        getBuilder(WRSounds.ENTITY_ALPINE_IDLE.value())
                .subtitle("sound.wyrmroost.entity.alpine.idle")
                .sounds(Wyrmroost::rl, "entity/alpine/%s", "idle1", "idle2")
                .build(json);
        getBuilder(WRSounds.ENTITY_ALPINE_HURT.value())
                .subtitle("sound.wyrmroost.entity.alpine.hurt")
                .sounds(Wyrmroost::rl, "entity/alpine/%s", "hurt1", "hurt2", "hurt3")
                .build(json);
        getBuilder(WRSounds.ENTITY_ALPINE_ROAR.value())
                .subtitle("sound.wyrmroost.entity.alpine.roar")
                .sounds(Wyrmroost::rl, "entity/alpine/%s", "roar", "roar1", "roar2")
                .build(json);
        getBuilder(WRSounds.ENTITY_ALPINE_DEATH.value())
                .subtitle("sound.wyrmroost.entity.alpine.death").sound(Wyrmroost.rl("entity/alpine/death"))
                .build(json);

        getBuilder(WRSounds.ENTITY_COINDRAGON_IDLE.value())
                .subtitle("sound.wyrmroost.entity.coin_dragon.idle")
                .sounds(Wyrmroost::rl, "entity/coin_dragon/%s", "idle", "idle1", "idle2")
                .build(json);

//        getBuilder(WRSounds.ENTITY_ORBWYRM_IDLE.value()
//        .subtitle("Orbwyrm Hissing")
//        .sounds(Wyrmroost::rl, "entity/orbwyrm/%s", "idle1", "idle2", "idle3")
//        .build(json);
//        getBuilder(WRSounds.ENTITY_ORBWYRM_HURT.value()
//        .subtitle("Orbwyrm Screech")
//        .sounds(Wyrmroost::rl, "entity/orbwyrm/%s", "hurt1", "hurt2", "hurt3")
//        .build(json);
//        getBuilder(WRSounds.ENTITY_ORBWYRM_HISS.value()
//        .subtitle("Orbwyrm Hiss").sound(Wyrmroost.rl("entity/orbwyrm/hiss"))
//        .build(json);
//        getBuilder(WRSounds.ENTITY_ORBWYRM_DEATH.value()
//        .subtitle("Orbwyrm Cry").sound(Wyrmroost.rl("entity/orbwyrm/death"))
//        .build(json);
    }

    private Builder getBuilder(SoundEvent sound)
    {
        soundBuilder.sound = sound;
        soundBuilder.json = new JsonObject();
        return soundBuilder;
    }

    @Override
    public String getName()
    {
        return "WR Sounds";
    }

    private class Builder
    {
        private SoundEvent sound;
        private JsonObject json;

        public Builder category(SoundSource category)
        {
            json.addProperty("category", category.getName());
            return this;
        }

        public Builder subtitle(String subtitle)
        {
            json.addProperty("subtitle", subtitle);
            return this;
        }

        public Builder sound(ResourceLocation sound, double volume, double pitch)
        {
            Preconditions.checkArgument(existingFileHelper.exists(sound, PackType.CLIENT_RESOURCES, ".ogg", "sounds"),
                    "Sound does not exist in any known Resourcepack: %s", sound);

            JsonArray array;
            boolean flag = false;
            if (json.has("sounds")) array = json.getAsJsonArray("sounds");
            else
            {
                array = new JsonArray();
                flag = true;
            }

            JsonObject object = new JsonObject();
            object.addProperty("name", sound.toString());
            if (volume != 1) object.addProperty("volume", volume);
            if (pitch != 1) object.addProperty("pitch", pitch);
            array.add(object);

            if (flag) json.add("sounds", array);

            return this;
        }

        public Builder sound(ResourceLocation path)
        {
            return sound(path, 1, 1);
        }

        public Builder sounds(Function<String, ResourceLocation> rlFunction, String path, String... sounds)
        {
            for (String s : sounds) sound(rlFunction.apply(path.replace("%s", s)), 1, 1);
            return this;
        }

        public void build(JsonObject soundsFile)
        {
            soundsFile.add(sound.getLocation().getPath(), json);
            REGISTERED.add(sound);
        }
    }
}
