package com.ultramega.rsinsertexportupgrade.inventory.item;

import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ConfiguredItemsInUpgradeItemHandler extends ItemStackHandler {
    private final ItemStack stack;

    public ConfiguredItemsInUpgradeItemHandler(ItemStack stack) {
        super(18);
        this.stack = stack;
        if (stack.hasTag()) {
            StackUtils.readItems(this, 0, stack.getTag());
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (!this.stack.hasTag()) {
            this.stack.setTag(new CompoundTag());
        }

        StackUtils.writeItems(this, 0, this.stack.getTag());
    }

    public NonNullList<ItemStack> getConfiguredItems() {
        return this.stacks;
    }
}
