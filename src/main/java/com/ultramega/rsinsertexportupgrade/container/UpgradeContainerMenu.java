package com.ultramega.rsinsertexportupgrade.container;

import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.ultramega.rsinsertexportupgrade.inventory.item.ConfiguredItemsInUpgradeItemHandler;
import com.ultramega.rsinsertexportupgrade.registry.ModMenuTypes;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UpgradeContainerMenu extends BaseContainerMenu {
    private final UpgradeType type;
    private final ItemStack upgradeItem;

    public UpgradeContainerMenu(UpgradeType type, Player player, ItemStack upgradeItem, int windowId) {
        super(type == UpgradeType.INSERT ? ModMenuTypes.INSERT_UPGRADE.get() : ModMenuTypes.EXPORT_UPGRADE.get(), null, player, windowId);
        this.type = type;
        this.upgradeItem = upgradeItem;

        int y = 20;
        int x = 8;

        ConfiguredItemsInUpgradeItemHandler upgrade = new ConfiguredItemsInUpgradeItemHandler(upgradeItem);

        addPlayerInventory(8, 81);

        for (int i = 0; i < 18; ++i) {
            addSlot(new FilterSlot(upgrade, i, x, y));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }
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

    @Override
    protected int getDisabledSlotNumber() {
        return getPlayer().getInventory().selected;
    }
}
