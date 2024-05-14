package com.ultramega.rsinsertexportupgrade.util;

import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IWhitelistBlacklist {
    static boolean acceptsItem(List<ItemStack> filters, int mode, int compare, ItemStack stack) {
        for (ItemStack slot : filters) {
            if (API.instance().getComparer().isEqual(slot, stack, compare)) {
                return mode == IFilter.MODE_WHITELIST;
            }
        }

        return mode != IFilter.MODE_WHITELIST;
    }
}
