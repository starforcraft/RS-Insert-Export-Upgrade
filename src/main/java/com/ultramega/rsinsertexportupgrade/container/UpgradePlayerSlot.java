package com.ultramega.rsinsertexportupgrade.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class UpgradePlayerSlot extends Slot {
    private boolean cancelPickup = false;

    public UpgradePlayerSlot(Container inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        if(cancelPickup) {
            cancelPickup = false;
            return false;
        } else {
            return true;
        }
    }

    public void setCancelPickup(boolean cancelPickup) {
        this.cancelPickup = cancelPickup;
    }
}
