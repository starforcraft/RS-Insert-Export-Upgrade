package com.ultramega.rsinsertexportupgrade.screen;

import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.container.UpgradePlayerSlot;
import com.ultramega.rsinsertexportupgrade.item.UpgradeItem;
import com.ultramega.rsinsertexportupgrade.network.LockSlotUpdateMessage;
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
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class UpgradeScreen extends BaseScreen<UpgradeContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/gui/upgrade.png");
    private static final ResourceLocation CHECKMARK = new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/gui/checkmark.png");
    private static final ResourceLocation XMARK = new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/gui/xmark.png");

    private final UpgradeType type;
    private Button modeButton;

    private final int[] selectedInventorySlots;
    private int compare;
    private int mode;
    private boolean cancel = false;
    private boolean dragging = false;
    private int clickedSlotId = -1;

    private int timeSinceRendered = 0;

    public UpgradeScreen(UpgradeType type, UpgradeContainerMenu containerMenu, Inventory inventory, Component title) {
        super(containerMenu, type == UpgradeType.EXPORT ? 210 : 176, 163, inventory, title);

        this.type = type;
        this.compare = UpgradeItem.getCompare(containerMenu.getUpgradeItem());
        this.mode = UpgradeItem.getMode(containerMenu.getUpgradeItem());
        this.selectedInventorySlots = UpgradeItem.getSelectedInventorySlots(containerMenu.getUpgradeItem());
    }

    @Override
    public void onPostInit(int x, int y) {
        addCheckBox(x + (type == UpgradeType.EXPORT ? 135 : 80), y + 63, Component.translatable("gui.refinedstorage.filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT, btn -> {
            compare ^= IComparer.COMPARE_NBT;

            sendUpdate();
        });

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
        if(timeSinceRendered < 20) {
            timeSinceRendered++;
        }

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        for (int i = 0; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.slots.get(i);

            int index = i - 18 - (type == UpgradeType.EXPORT ? 2 : 0);

            if (slot instanceof FilterSlot) {
                if (type != UpgradeType.INSERT) {
                    renderSlotHighlight(graphics, type, font, slot.x + leftPos, slot.y + topPos, true, i - 2 + 1);
                }
            } else if(i >= 18 && selectedInventorySlots.length > index) {
                if (selectedInventorySlots[index] >= 1) {
                    renderSlotHighlight(graphics, type, font, slot.x + leftPos, slot.y + topPos, true, selectedInventorySlots[index]);
                } else if (selectedInventorySlots[index] == 0) {
                    renderSlotHighlight(graphics, type, font, slot.x + leftPos, slot.y + topPos, false, -1);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
        if(itemstack.isEmpty()) {
            Slot slot = findSlot(mouseX, mouseY);
            if (slot instanceof UpgradePlayerSlot) {
                if(hasShiftDown() && !slot.getItem().isEmpty()) {
                    cancel = true;
                }
                if(!cancel) {
                    clickedSlotId = slot.index;
                    RSInsertExportUpgrade.NETWORK_HANDLER.sendToServer(new LockSlotUpdateMessage(clickedSlotId, true));
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = findSlot(mouseX, mouseY);
        if(!cancel && slot instanceof UpgradePlayerSlot) {
            ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
            if((!dragging || (slot.getItem().isEmpty() && slot.index == clickedSlotId)) && itemstack.isEmpty() && timeSinceRendered >= 20) {
                int slotId = slot.index - (18 + (type == UpgradeType.EXPORT ? 2 : 0));

                if (this.type == UpgradeType.INSERT) {
                    selectedInventorySlots[slotId] = selectedInventorySlots[slotId] == 0 ? 1 : 0;
                } else {
                    if (button == 0) {
                        //Left click
                        if (selectedInventorySlots[slotId] >= 18) {
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

        cancel = false;
        dragging = false;
        clickedSlotId = -1;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        dragging = true;
        ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
        if(clickedSlotId != -1 && itemstack.isEmpty()) {
            slotClicked(this.menu.slots.get(clickedSlotId), clickedSlotId, button, ClickType.PICKUP);
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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
        RSInsertExportUpgrade.NETWORK_HANDLER.sendToServer(new UpgradeUpdateMessage(type.getId(), compare, mode, selectedInventorySlots, getMenu().selectedSideButton));
    }
}
