package com.ultramega.rsinsertexportupgrade.inventory.item;

import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ConfiguredItemsInFilterItemHandler extends ItemStackHandler {
    private final CompoundTag tag;

    public ConfiguredItemsInFilterItemHandler(CompoundTag tag) {
        super(18);
        this.tag = tag;

        StackUtils.readItems(this, 0, tag);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        StackUtils.writeItems(this, 0, tag);
    }

    public NonNullList<ItemStack> getConfiguredItems() {
        return stacks;
    }
}

