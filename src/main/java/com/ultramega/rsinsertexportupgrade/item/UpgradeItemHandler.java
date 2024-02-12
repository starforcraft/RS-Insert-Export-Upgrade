package com.ultramega.rsinsertexportupgrade.item;

import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.ultramega.rsinsertexportupgrade.inventory.item.ConfiguredItemsInUpgradeItemHandler;
import com.ultramega.rsinsertexportupgrade.registry.ModItems;
import com.ultramega.rsinsertexportupgrade.util.ItemUpgrade;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class UpgradeItemHandler extends BaseItemHandler {
    public final List<IFilter> filters;

    public UpgradeItemHandler(List<IFilter> filters) {
        super(2);
        this.filters = filters;

        this.addValidator(new ItemValidator(ModItems.INSERT_UPGRADE.get())).addValidator(new ItemValidator(ModItems.EXPORT_UPGRADE.get()));
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        filters.clear();

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack filterItem = getStackInSlot(i);
            if (!filterItem.isEmpty()) {
                handleFilterItem(filterItem);
            }
        }
    }

    private void handleFilterItem(ItemStack upgradeItem) {
        int mode = UpgradeItem.getMode(upgradeItem);
        int[] selectedInventorySlots = UpgradeItem.getSelectedInventorySlots(upgradeItem);

        List<IFilter> foundFilters = new ArrayList<>();

        for (ItemStack stack : new ConfiguredItemsInUpgradeItemHandler(upgradeItem).getConfiguredItems()) {
            if (stack.getItem() == ModItems.INSERT_UPGRADE.get() || stack.getItem() == ModItems.EXPORT_UPGRADE.get()) {
                handleFilterItem(stack);
            } else if (!stack.isEmpty()) {
                foundFilters.add(new ItemUpgrade(mode, selectedInventorySlots));
            }
        }

        filters.addAll(foundFilters);
    }
}
