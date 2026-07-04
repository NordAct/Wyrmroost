package com.github.wolfshotz.wyrmroost.client.screen;

import com.github.wolfshotz.wyrmroost.client.screen.widgets.NameFieldWidget;
import com.github.wolfshotz.wyrmroost.client.screen.widgets.StaffActionButton;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.items.staff.DragonStaffItem;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StaffScreen extends Screen
{
    private final AbstractDragonEntity dragon;
    public final List<FormattedCharSequence> toolTip = new ArrayList<>();
    public final List<StaffAction> actions = new ArrayList<>();

    public StaffScreen(AbstractDragonEntity dragon)
    {
        super(Component.literal("Staff Screen"));
        this.dragon = dragon;
    }

    @Override
    protected void init()
    {
        actions.clear();
        toolTip.clear();
        dragon.addScreenInfo(this);

        addWidget(new NameFieldWidget(font, (width / 2) - 63, (height / 2) + 25, 120, 12, dragon));

        initActions();
    }

    public void initActions()
    {
        int size = actions.size();
        int radius = Math.max(size * 20, 100);
        for (int i = 0; i < size; i++)
        {
            StaffAction action = actions.get(i);
            Component name = action.getTranslation(dragon);
            double deg = 2 * Math.PI * i / size - Math.toRadians(90);
            int x = (int) (radius * Math.cos(deg));
            int y = (int) (radius * Math.sin(deg));
            x += (width / 2) - 50;
            y += (height / 2) - 10;
            addWidget(new StaffActionButton(x, y, name, action));
        }
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks)
    {
        super.render(ms, mouseX, mouseY, partialTicks);
        int x = width / 2;
        int y = (height / 2) + (int) (dragon.getBbHeight() / 2);

        if (dragon.getVariant() < 0)
            ms.drawString(font, Character.toString('\u2726'), x - 40, y - 40, 0xffff00);

        int scale = (int) -(dragon.getBbWidth() * dragon.getBbHeight()) + 23; // linear decay: smaller scale bigger the dragon. if things get problematic, exponential?
        InventoryScreen.renderEntityInInventoryFollowsMouse(ms, x - 100, y - 100, x + 100, y + 100, scale, 0, x - mouseX, y - mouseY, dragon);
        if (mouseX >= x - 40 && mouseY >= y - 40 && mouseX < x + 45 && mouseY < y + 15)
            ms.renderTooltip(font, toolTip, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    public void addAction(StaffAction action) { actions.add(action); }

    public void addTooltip(String string) { toolTip.add(FormattedCharSequence.forward(string, Style.EMPTY)); }

    public static void open(AbstractDragonEntity dragon, ItemStack stack)
    {
        DragonStaffItem.bindDragon(dragon, stack);
        if (dragon.level().isClientSide()) Minecraft.getInstance().setScreen(new StaffScreen(dragon));
    }
}
