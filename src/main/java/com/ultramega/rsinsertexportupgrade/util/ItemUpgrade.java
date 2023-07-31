package com.ultramega.rsinsertexportupgrade.util;

import com.refinedmods.refinedstorage.api.util.IFilter;

public record ItemUpgrade(int mode, int[] selectedInventorySlots) implements IFilter {
    @Override
    public Object getStack() {
        return null;
    }

    @Override
    public int getCompare() {
        return 0;
    }

    @Override
    public int getMode() {
        return 0;
    }

    @Override
    public boolean isModFilter() {
        return false;
    }
}
