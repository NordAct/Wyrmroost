package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;

// Wolf, they unfucked creative mode tabs, you can come back - Nord
public class WRCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Wyrmroost.MOD_ID);

    public static final Holder<CreativeModeTab> CREATIVE_MODE_TAB_HOLDER = REGISTRY.register(
            "creative_tab",
            () -> {
                CreativeModeTab.Builder builder = CreativeModeTab.builder();
                builder.icon(() -> WRItems.BLUE_GEODE.value().getDefaultInstance());
                builder.displayItems(WRItems.REGISTRY.getEntries());
                if (WRConfig.debugMode) {
                    builder.displayItems((parameters, o) -> {
                        ItemStack stack = new ItemStack(Items.STICK);
                        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Debug Stick"));
                        o.accept(stack);
                    });
                }
                return builder.build();
            }
    );
}
