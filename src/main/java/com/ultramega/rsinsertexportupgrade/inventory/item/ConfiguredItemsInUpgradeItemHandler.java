package com.ultramega.rsinsertexportupgrade.inventory.item;

import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ConfiguredItemsInUpgradeItemHandler extends ItemStackHandler {
    private final ItemStack gridStack;
    private final ItemStack upgradeStack;
    private final int selectedSideButton;

    public ConfiguredItemsInUpgradeItemHandler(ItemStack gridStack, ItemStack upgradeStack, int selectedSideButton) {
        super(18);
        this.gridStack = gridStack;
        this.upgradeStack = upgradeStack;
        this.selectedSideButton = selectedSideButton;
        if (upgradeStack.hasTag()) {
            StackUtils.readItems(this, 0, upgradeStack.getTag());
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (selectedSideButton != -1 && this.gridStack != null && this.gridStack.hasTag() && gridStack.getTag().contains("Inventory_1")) {
            ListTag tagList = gridStack.getTag().getList("Inventory_1", Tag.TAG_COMPOUND);
            CompoundTag tag = (CompoundTag) tagList.stream()
                    .filter(x -> x.toString().contains("Slot:" + selectedSideButton))
                    .findFirst()
                    .orElse(null);

            if (tag != null) {
                if(!tag.contains("tag")) {
                    tag.put("tag", new CompoundTag());
                }

                StackUtils.writeItems(this, 0, tag.getCompound("tag"));
            }
        } else if(this.upgradeStack != null && this.upgradeStack.hasTag()) {
            StackUtils.writeItems(this, 0, upgradeStack.getTag());
        }
    }

    public NonNullList<ItemStack> getConfiguredItems() {
        return this.stacks;
    }
}
