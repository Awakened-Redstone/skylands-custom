package com.awakenedredstone.neoskies.gui;

import com.awakenedredstone.neoskies.api.island.IslandSettingsManager;
import com.awakenedredstone.neoskies.gui.polymer.CBGuiElement;
import com.awakenedredstone.neoskies.gui.polymer.CBGuiElementBuilder;
import com.awakenedredstone.neoskies.gui.polymer.CBSimpleGuiBuilder;
import com.awakenedredstone.neoskies.util.UIUtils;
import eu.pb4.sgui.api.gui.GuiInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import com.awakenedredstone.neoskies.api.island.IslandSettings;
import com.awakenedredstone.neoskies.logic.Island;
import com.awakenedredstone.neoskies.util.Texts;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class IslandSettingsGui {
    private final Island island;
    private final GuiInterface parent;
    private final List<Map.Entry<Identifier, IslandSettings>> entries;
    private final Consumer<SlotGuiInterface> updateGui;
    private final Consumer<SlotGuiInterface> simpleUpdateGui;
    private int page = 0;

    private final CBGuiElement filler = new CBGuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE).setName(Text.empty()).hideTooltip().build();
    private final CBGuiElement nextPage = new CBGuiElementBuilder(Items.LIME_STAINED_GLASS_PANE).setName(Texts.of("neoskies.page.next")).setCallback((index, type, action, gui) -> offsetPage(1, gui)).build();
    private final CBGuiElement prevPage = new CBGuiElementBuilder(Items.RED_STAINED_GLASS_PANE).setName(Texts.of("neoskies.page.previous")).setCallback((index, type, action, gui) -> offsetPage(-1, gui)).build();


    public IslandSettingsGui(Island island, @Nullable GuiInterface parent) {
        this.island = island;
        this.parent = parent;
        this.entries = island.getSettings().entrySet().stream().toList();

        updateGui = gui -> {
            UIUtils.fillGui(gui, filler);

            gui.setTitle(Texts.of("gui.neoskies.island_settings"));

            int slot = 10;
            int offset = page * 28;
            for (int i = offset; i < Math.min(offset + 28, island.getSettings().size()); i++) {
                if ((slot + 1) % 9 == 0 && slot > 10) slot += 2;
                Map.Entry<Identifier, IslandSettings> pageEntry = entries.get(i);
                gui.setSlot(slot++, IslandSettingsManager.getIcon(pageEntry.getKey(), island));
            }

            if (page < getPageMax()) gui.setSlot(gui.getSize() - 8, nextPage);
            if (page > 0) gui.setSlot(gui.getSize() - 9, prevPage);

            CBGuiElementBuilder close = new CBGuiElementBuilder(Items.BARRIER)
                    .setName(Texts.of("gui.neoskies.close"))
                    .setCallback((index, type, action, gui1) -> {
                        gui.getPlayer().playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.3f, 1);
                        if (parent != null) {
                            parent.close();
                            parent.open();
                        } else gui.close();
                    });
            gui.setSlot(gui.getSize() - 1, close);
        };

        simpleUpdateGui = gui -> {
            int slot = 10;
            int offset = page * 28;
            for (int i = offset; i < Math.min(offset + 28, island.getSettings().size()); i++) {
                if ((slot + 1) % 9 == 0 && slot > 10) slot += 2;
                Map.Entry<Identifier, IslandSettings> pageEntry = entries.get(i);
                gui.setSlot(slot++, IslandSettingsManager.getIcon(pageEntry.getKey(), island));
            }
        };
    }

    public SimpleGui buildGui(ServerPlayerEntity player) {
        CBSimpleGuiBuilder builder = new CBSimpleGuiBuilder(ScreenHandlerType.GENERIC_9X6, false);
        UIUtils.fillGui(builder, filler);

        builder.setOnOpen(updateGui::accept);
        builder.setOnClick(simpleUpdateGui::accept);

        return builder.build(player);
    }

    public void openGui(ServerPlayerEntity player) {
        buildGui(player).open();
    }

    public void offsetPage(int offset, SlotGuiInterface gui) {
        gui.getPlayer().playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.3f, 1);
        this.page = (int) MathHelper.clamp(this.page + offset, 0, Math.floor(island.getSettings().size() / 28f));
        updateGui.accept(gui);
    }

    private int getPageMax() {
        return (int) Math.ceil(island.getSettings().size() / 28f) - 1;
    }
}
