package com.ultramega.rsinsertexportupgrade.screen;

import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.item.UpgradeItem;
import com.ultramega.rsinsertexportupgrade.network.UpgradeUpdateMessage;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

import java.awt.*;

public class UpgradeScreen extends BaseScreen<UpgradeContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/gui/upgrade.png");
    private static final Color[] slotColors = { Color.RED, new Color(28, 134, 238), new Color(124, 252, 0), new Color(106, 61, 154), new Color(255, 127, 0), new Color(166, 124, 0), new Color(126, 192, 238), new Color(251, 154, 153), new Color(144, 238, 144),
                                                new Color(202, 178, 214), new Color(253, 191, 111), new Color(139, 139, 0), new Color(238, 230, 133), new Color(176, 48, 96), new Color(255,131,250), new Color(255, 20, 147), new Color(0, 0, 255), new Color(165, 42, 42) };

    private int mode;
    private final int[] selectedInventorySlots;
    private final UpgradeType upgradeType;

    private Button modeButton;

    public UpgradeScreen(UpgradeType upgradeType, UpgradeContainerMenu containerMenu, Inventory inventory, Component title) {
        super(containerMenu, 176, 163, inventory, title);

        this.upgradeType = upgradeType;
        this.mode = UpgradeItem.getMode(containerMenu.getUpgradeItem());
        this.selectedInventorySlots = UpgradeItem.getSelectedInventorySlots(containerMenu.getUpgradeItem());
    }

    @Override
    public void onPostInit(int x, int y) {
        if(upgradeType == UpgradeType.INSERT) {
            modeButton = addButton(x + 118, y + 58, 0, 20, Component.literal(""), true, true, btn -> {
                mode = mode == IFilter.MODE_WHITELIST ? IFilter.MODE_BLACKLIST : IFilter.MODE_WHITELIST;

                updateModeButton(x, mode);

                sendUpdate();
            });

            updateModeButton(x, mode);
        }
    }

    private void updateModeButton(int x, int mode) {
        Component text = mode == IFilter.MODE_WHITELIST ? Component.translatable("sidebutton.refinedstorage.mode.whitelist") : Component.translatable("sidebutton.refinedstorage.mode.blacklist");

        modeButton.setWidth(font.width(text.getString()) + 12);
        modeButton.setX(x + 118 -(mode == IFilter.MODE_BLACKLIST ? 2 : 0));
        modeButton.setMessage(text);
    }


    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        for(int i = 0; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.slots.get(i);

            if(slot instanceof FilterSlot) {
                if(upgradeType != UpgradeType.INSERT) {
                    renderSlotHighlight(graphics, slot.x + leftPos, slot.y + topPos, 0, slotColors[i - 36].hashCode());
                }
            } else if(selectedInventorySlots[i] >= 1) {
                renderSlotHighlight(graphics, slot.x + leftPos, slot.y + topPos, 0, slotColors[selectedInventorySlots[i] - 1].hashCode());
            }
        }
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);

        if(!(slot instanceof FilterSlot) && slotId >= 0 && type != ClickType.PICKUP_ALL) {
            if(upgradeType == UpgradeType.INSERT) {
                selectedInventorySlots[slotId] = selectedInventorySlots[slotId] == 0 ? 1 : 0;
            } else {
                if (mouseButton == 0) {
                    //Left click
                    if(selectedInventorySlots[slotId] >= slotColors.length) {
                        selectedInventorySlots[slotId] = 0;
                    } else {
                        selectedInventorySlots[slotId] += 1;
                    }
                } else {
                    //Right click
                    selectedInventorySlots[slotId] = 0;
                }
            }

            sendUpdate();
        }
    }

    public static void renderSlotHighlight(GuiGraphics graphics, int x, int y, int blitOffset, int color) {
        graphics.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, color, color, blitOffset);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());
        renderString(graphics, 7, 69, I18n.get("container.inventory"));
    }

    public void sendUpdate() {
        RSInsertExportUpgrade.NETWORK_HANDLER.sendToServer(new UpgradeUpdateMessage(upgradeType.getId(), mode, selectedInventorySlots));
    }
}
