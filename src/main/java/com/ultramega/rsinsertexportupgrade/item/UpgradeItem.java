package com.ultramega.rsinsertexportupgrade.item;

import com.refinedmods.refinedstorage.api.util.IFilter;
import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.registry.ModItems;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";
    public static final String NBT_SELECTED_INVENTORY_SLOTS = "SelectedInventorySlots";

    private final UpgradeType type;

    public UpgradeItem(UpgradeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public static int getCompare(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_COMPARE)) ? stack.getTag().getInt(NBT_COMPARE) : IFilter.MODE_BLACKLIST;
    }

    public static void setCompare(ItemStack stack, int compare, int selectedSideButton) {
        setNBT(stack, compare, selectedSideButton, NBT_COMPARE);
    }

    public static int getMode(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_MODE)) ? stack.getTag().getInt(NBT_MODE) : IFilter.MODE_BLACKLIST;
    }

    public static void setMode(ItemStack stack, int mode, int selectedSideButton) {
        setNBT(stack, mode, selectedSideButton, NBT_MODE);
    }

    private static void setNBT(ItemStack stack, int value, int selectedSideButton, String nbt) {
        if (stack.getItem() == ModItems.INSERT_UPGRADE.get() || stack.getItem() == ModItems.EXPORT_UPGRADE.get()) {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
            }

            stack.getTag().putInt(nbt, value);
        } else {
            if (stack.getTag().contains("Inventory_1")) {
                ListTag tagList = stack.getTag().getList("Inventory_1", Tag.TAG_COMPOUND);
                CompoundTag tag = (CompoundTag) tagList.stream()
                        .filter(x -> x.toString().contains("Slot:" + selectedSideButton))
                        .findFirst()
                        .orElse(null);

                if(tag != null) {
                    if(!tag.contains("tag")) {
                        tag.put("tag", new CompoundTag());
                    }

                    ((CompoundTag) tag.get("tag")).putInt(nbt, value);
                }
            }
        }
    }

    public static int[] getSelectedInventorySlots(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_SELECTED_INVENTORY_SLOTS)) ? stack.getTag().getIntArray(NBT_SELECTED_INVENTORY_SLOTS) : new int[36];
    }

    public static void setSelectedInventorySlots(ItemStack stack, int[] selectedInventorySlots, int selectedSideButton) {
        if (stack.getItem() == ModItems.INSERT_UPGRADE.get() || stack.getItem() == ModItems.EXPORT_UPGRADE.get()) {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
            }

            stack.getTag().putIntArray(NBT_SELECTED_INVENTORY_SLOTS, selectedInventorySlots);
        } else {
            if (stack.getTag().contains("Inventory_1")) {
                ListTag tagList = stack.getTag().getList("Inventory_1", Tag.TAG_COMPOUND);
                CompoundTag tag = (CompoundTag) tagList.stream()
                        .filter(x -> x.toString().contains("Slot:" + selectedSideButton))
                        .findFirst()
                        .orElse(null);

                if(tag != null) {
                    if(!tag.contains("tag")) {
                        tag.put("tag", new CompoundTag());
                    }

                    ((CompoundTag) tag.get("tag")).putIntArray(NBT_SELECTED_INVENTORY_SLOTS, selectedInventorySlots);
                }
            }
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
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
                    return new UpgradeContainerMenu(type, player, inventory.getSelected(), windowId, -1);
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
