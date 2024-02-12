package com.ultramega.rsinsertexportupgrade.screen;

import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.item.UpgradeItem;
import com.ultramega.rsinsertexportupgrade.network.UpgradeUpdateMessage;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

import java.awt.*;

public class UpgradeScreen extends BaseScreen<UpgradeContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/gui/upgrade.png");
    private static final ResourceLocation CHECKMARK = new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/gui/checkmark.png");
    private static final ResourceLocation XMARK = new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/gui/xmark.png");
    private static final Color[] slotColors = {Color.RED, new Color(28, 134, 238), new Color(124, 252, 0), new Color(106, 61, 154), new Color(255, 127, 0), new Color(166, 124, 0), new Color(126, 192, 238), new Color(251, 154, 153), new Color(144, 238, 144),
            new Color(202, 178, 214), new Color(253, 191, 111), new Color(139, 139, 0), new Color(238, 230, 133), new Color(176, 48, 96), new Color(255, 131, 250), new Color(255, 20, 147), new Color(0, 0, 255), new Color(165, 42, 42)};

    private int mode;
    private final int[] selectedInventorySlots;
    private final UpgradeType type;

    private Button modeButton;

    public UpgradeScreen(UpgradeType type, UpgradeContainerMenu containerMenu, Inventory inventory, Component title) {
        super(containerMenu, 176, 163, inventory, title);

        this.type = type;
        this.mode = UpgradeItem.getMode(containerMenu.getUpgradeItem());
        this.selectedInventorySlots = UpgradeItem.getSelectedInventorySlots(containerMenu.getUpgradeItem());
    }

    @Override
    public void onPostInit(int x, int y) {
        if (type == UpgradeType.INSERT) {
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
        modeButton.setX(x + 118 - (mode == IFilter.MODE_BLACKLIST ? 2 : 0));
        modeButton.setMessage(text);
    }


    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        for (int i = 0; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.slots.get(i);

            if (slot instanceof FilterSlot) {
                if (type != UpgradeType.INSERT) {
                    renderSlotHighlight(graphics, type, font, slot.x + leftPos, slot.y + topPos/*, slotColors[i - 36].hashCode()*/, true, i - 36 + 1);
                }
            } else if (selectedInventorySlots[i] >= 1) {
                renderSlotHighlight(graphics, type, font, slot.x + leftPos, slot.y + topPos/*, slotColors[selectedInventorySlots[i] - 1].hashCode()*/, true, selectedInventorySlots[i]);
            } else if (selectedInventorySlots[i] == 0) {
                renderSlotHighlight(graphics, type, font, slot.x + leftPos, slot.y + topPos/*, slotColors[selectedInventorySlots[i] - 1].hashCode()*/, false, -1);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);

        if (!(slot instanceof FilterSlot) && slotId >= 0 && type != ClickType.PICKUP_ALL) {
            if (this.type == UpgradeType.INSERT) {
                selectedInventorySlots[slotId] = selectedInventorySlots[slotId] == 0 ? 1 : 0;
            } else {
                if (mouseButton == 0) {
                    //Left click
                    if (selectedInventorySlots[slotId] >= slotColors.length) {
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

    public static void renderSlotHighlight(GuiGraphics graphics, UpgradeType type, Font font, int x, int y, boolean checked, int filterIndex) {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300.0F);

        if (checked) {
            if (type == UpgradeType.INSERT) {
                graphics.blit(CHECKMARK, x + 7, y, 0, 0, 9, 8, 9, 8);
            } else {
                graphics.drawString(font, String.valueOf(filterIndex), x + 16 - font.width(String.valueOf(filterIndex)), y, Color.GREEN.hashCode());
            }
        } else {
            graphics.blit(XMARK, x + 9, y, 0, 0, 7, 7, 7, 7);
        }

        graphics.pose().popPose();
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());
        renderString(graphics, 7, 69, I18n.get("container.inventory"));
    }

    public void sendUpdate() {
        RSInsertExportUpgrade.NETWORK_HANDLER.sendToServer(new UpgradeUpdateMessage(type.getId(), mode, selectedInventorySlots, getMenu().selectedSideButton));
    }
}
