package com.ultramega.rsinsertexportupgrade.item;

import com.refinedmods.refinedstorage.api.util.IFilter;
import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.registry.ModItems;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class UpgradeItem extends Item {
    public static final String NBT_MODE = "Mode";
    public static final String NBT_SELECTED_INVENTORY_SLOTS = "SelectedInventorySlots";

    private final UpgradeType type;

    public UpgradeItem(UpgradeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public static int getMode(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_MODE)) ? stack.getTag().getInt(NBT_MODE) : IFilter.MODE_WHITELIST;
    }

    public static void setMode(ItemStack stack, int mode) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putInt(NBT_MODE, mode);
    }

    public static int[] getSelectedInventorySlots(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_SELECTED_INVENTORY_SLOTS)) ? stack.getTag().getIntArray(NBT_SELECTED_INVENTORY_SLOTS) : new int[36];
    }

    public static void setSelectedInventorySlots(ItemStack stack, int[] selectedInventorySlots) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putIntArray(NBT_SELECTED_INVENTORY_SLOTS, selectedInventorySlots);
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (player.isCrouching()) {
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, new ItemStack(type == UpgradeType.INSERT ? ModItems.INSERT_UPGRADE.get() : ModItems.EXPORT_UPGRADE.get()));
            }

            player.openMenu(new MenuProvider() {
                @NotNull
                @Override
                public Component getDisplayName() {
                    return Component.translatable("item.rsinsertexportupgrade." + type.getName() + "_upgrade");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
                    return new UpgradeContainerMenu(type, player, inventory.getSelected(), windowId);
                }
            });
        }

        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }


    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}