package com.ultramega.rsinsertexportupgrade.container;

import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.ultramega.rsinsertexportupgrade.inventory.item.ConfiguredItemsInUpgradeItemHandler;
import com.ultramega.rsinsertexportupgrade.registry.ModItems;
import com.ultramega.rsinsertexportupgrade.registry.ModMenuTypes;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class UpgradeContainerMenu extends BaseContainerMenu {
    private final UpgradeType type;
    private ItemStack upgradeItem;
    private ItemStack gridItem;
    public final int selectedSideButton;

    public UpgradeContainerMenu(UpgradeType type, Player player, ItemStack stack, int windowId, int selectedSideButton) {
        super(type == UpgradeType.INSERT ? ModMenuTypes.INSERT_UPGRADE.get() : ModMenuTypes.EXPORT_UPGRADE.get(), null, player, windowId);
        this.type = type;
        if (selectedSideButton != -1 && stack.getItem() != ModItems.INSERT_UPGRADE.get() && stack.getItem() != ModItems.EXPORT_UPGRADE.get()) {
            this.gridItem = stack;

            Container container = new SimpleContainer(2);
            StackUtils.readItems(container, 2, stack.getTag());

            if (container.getItem(selectedSideButton).getItem() == (type == UpgradeType.INSERT ? ModItems.INSERT_UPGRADE.get() : ModItems.EXPORT_UPGRADE.get())) {
                this.upgradeItem = container.getItem(selectedSideButton);
            }
        } else {
            this.upgradeItem = stack;
        }
        this.selectedSideButton = selectedSideButton;

        UpgradeItemHandler upgrades = new UpgradeItemHandler(2, type == UpgradeType.EXPORT ? new UpgradeItem.Type[]{ UpgradeItem.Type.STACK, UpgradeItem.Type.CRAFTING } : new UpgradeItem.Type[]{});

        if (upgradeItem.hasTag()) {
            StackUtils.readItems(upgrades, 1, upgradeItem.getTag());
        }
        upgrades.addListener((handler, slot, reading) -> {
            if (!reading) {
                if (selectedSideButton != -1 && this.gridItem != null && this.gridItem.hasTag() && gridItem.getTag().contains("Inventory_1")) {
                    ListTag tagList = gridItem.getTag().getList("Inventory_1", Tag.TAG_COMPOUND);
                    CompoundTag tag = (CompoundTag) tagList.stream()
                            .filter(x -> x.toString().contains("Slot:" + selectedSideButton))
                            .findFirst()
                            .orElse(null);

                    if (tag != null) {
                        if(!tag.contains("tag")) {
                            tag.put("tag", new CompoundTag());
                        }

                        StackUtils.writeItems(upgrades, 1, tag.getCompound("tag"));
                    }
                } else if(this.upgradeItem != null && this.upgradeItem.hasTag()) {
                    StackUtils.writeItems(upgrades, 1, upgradeItem.getTag());
                }
            }
        });

        if(type == UpgradeType.EXPORT) {
            for (int i = 0; i < 2; ++i) {
                addSlot(new SlotItemHandler(upgrades, i, 187, 6 + (i * 18)));
            }
        }

        ConfiguredItemsInUpgradeItemHandler filter = new ConfiguredItemsInUpgradeItemHandler(gridItem, upgradeItem, selectedSideButton);
        int x = 8;
        int y = 20;
        for (int i = 0; i < 18; ++i) {
            addSlot(new FilterSlot(filter, i, x, y));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addPlayerInventory(8, 81);

        transferManager.addBiTransfer(getPlayer().getInventory(), upgrades);
    }

    @Override
    protected void addPlayerInventory(int xInventory, int yInventory) {
        int id = 9;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new UpgradePlayerSlot(getPlayer().getInventory(), id, xInventory + x * 18, yInventory + y * 18));
                id++;
            }
        }

        id = 0;

        for (int i = 0; i < 9; i++) {
            int x = xInventory + i * 18;
            int y = yInventory + 4 + (3 * 18);

            addSlot(new UpgradePlayerSlot(getPlayer().getInventory(), id, x, y));
            id++;
        }
    }

    public ItemStack getUpgradeItem() {
        return upgradeItem;
    }

    public ItemStack getGridItem() {
        return gridItem == null ? upgradeItem : gridItem;
    }

    @Override
    protected int getDisabledSlotNumber() {
        return getPlayer().getInventory().selected;
    }
}
