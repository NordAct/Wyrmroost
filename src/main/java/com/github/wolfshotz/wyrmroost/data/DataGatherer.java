package com.github.wolfshotz.wyrmroost.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Data is data. It <I>could</I> be cleaner to integrate this data inside the registry logic and have the registry
 * object instances hold everything.
 * HOWEVER, I feel that, much like the "Seperate Client from Server" concept, I would like to keep
 * data related shit in its own space. Thus the DataGatherer
 * This is because, when it comes to runtime, the object instances that hold that data will never even be used.
 * Nitpicky, but I don't care its saving someone that little bit of memory to squeeze that one last chrome tab in.
 */
public class DataGatherer
{
    public static void gather(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        PackOutput output = gen.getPackOutput();

        TagData.provide(gen, event.includeServer(), event.getExistingFileHelper(), output, lookupProvider);
        gen.addProvider(event.includeServer(), new RecipeData(output, lookupProvider));
        gen.addProvider(event.includeServer(), new LootTableData(output, lookupProvider));
        gen.addProvider(event.includeServer(), new DamageTypeData(output, lookupProvider));
        gen.addProvider(event.includeServer(), new BiomeModifiersData(output, lookupProvider));
        gen.addProvider(event.includeServer(), new ConfiguredFeatureData(output, lookupProvider));
        gen.addProvider(event.includeServer(), new PlacedFeatureData(output, lookupProvider));

        ModelData.provide(gen, event.includeClient(), event.getExistingFileHelper(), output);
        gen.addProvider(event.includeClient(), new SoundData(output, event.getExistingFileHelper()));
    }
}
