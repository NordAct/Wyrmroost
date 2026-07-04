package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.registry.WRDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DamageTypeData implements DataProvider {
    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    private final Map<ResourceLocation, DamageType> holder = new HashMap<>();

    public DamageTypeData(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        this.output = output;
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return registryLookupFuture.thenCompose((provider) -> {
            addEntries(provider);
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach((key, value) -> {
                Path path =  output.createPathProvider(PackOutput.Target.DATA_PACK, Registries.DAMAGE_TYPE.location().getPath()).json(key);
                list.add(DataProvider.saveStable(cache, provider, DamageType.DIRECT_CODEC, value, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected void addEntry(ResourceLocation id, DamageType entry) {
        holder.put(id, entry);
    }

    public void addEntries(HolderLookup.Provider provider) {
        addEntry(WRDamageTypes.WIND_GUST.location(), new DamageType("windGust", 0));
        addEntry(WRDamageTypes.FIRE_BREATH_0.location(), new DamageType("fireBreath0", 0.1f));
        addEntry(WRDamageTypes.FIRE_BREATH_1.location(), new DamageType("fireBreath1", 0.1f));
    }

    @Override
    public String getName() {
        return "Wyrmroost Damage Types";
    }
}
