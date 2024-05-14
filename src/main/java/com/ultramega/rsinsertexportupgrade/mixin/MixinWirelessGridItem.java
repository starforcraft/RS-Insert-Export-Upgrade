package com.ultramega.rsinsertexportupgrade.mixin;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphEntry;
import com.refinedmods.refinedstorage.api.network.IWirelessTransmitter;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.item.WirelessGridItem;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorageaddons.item.WirelessCraftingGridItem;
import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import com.ultramega.rsinsertexportupgrade.inventory.item.ConfiguredItemsInFilterItemHandler;
import com.ultramega.rsinsertexportupgrade.item.UpgradeItem;
import com.ultramega.rsinsertexportupgrade.util.IWhitelistBlacklist;
import com.ultramega.universalgrid.item.WirelessUniversalGridItem;
import net.gigabit101.rebornstorage.items.ItemWirelessGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.refinedmods.refinedstorage.item.NetworkItem.*;

@Mixin({WirelessGridItem.class, WirelessCraftingGridItem.class, WirelessUniversalGridItem.class, ItemWirelessGrid.class})
public abstract class MixinWirelessGridItem extends Item {
    protected MixinWirelessGridItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!stack.hasTag() || level.isClientSide)
            return;

        //Check if in transmitter range
        if (entity instanceof Player player) {
            boolean inRange = false;

            INetwork network = rsInsertExportUpgrade$getNetwork(level.getServer(), stack);

            if (network == null) return;

            for (INetworkNodeGraphEntry entry : network.getNodeGraph().all()) {
                INetworkNode node = entry.getNode();

                if (node instanceof IWirelessTransmitter transmitter && network.canRun() && node.isActive() && ((IWirelessTransmitter) node).getDimension() == player.getCommandSenderWorld().dimension()) {
                    Vec3 pos = player.position();

                    double distance = Math.sqrt(Math.pow(transmitter.getOrigin().getX() - pos.x(), 2) + Math.pow(transmitter.getOrigin().getY() - pos.y(), 2) + Math.pow(transmitter.getOrigin().getZ() - pos.z(), 2));

                    if (distance < transmitter.getRange()) {
                        inRange = true;

                        break;
                    }
                }
            }

            if (!inRange) return;

            if (stack.getTag().contains("Inventory_1")) {
                ListTag tagList = stack.getTag().getList("Inventory_1", Tag.TAG_COMPOUND);

                for (int i = 0; i < tagList.size(); i++) {
                    boolean isInsertUpgrade = tagList.getCompound(i).getString("id").equals(new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "insert_upgrade").toString());
                    CompoundTag tag = (CompoundTag) tagList.getCompound(i).get("tag");

                    if (tag != null) {
                        int[] selectedInventorySlots = tag.getIntArray(UpgradeItem.NBT_SELECTED_INVENTORY_SLOTS);
                        int mode = isInsertUpgrade ? tag.getInt(UpgradeItem.NBT_MODE) : -1;
                        int compare = tag.getInt(UpgradeItem.NBT_COMPARE);

                        for (int j = 0; j < selectedInventorySlots.length; j++) {
                            if (selectedInventorySlots[j] >= 1) {
                                int index = j <= 26 ? j + 9 : j - 27;
                                ItemStack itemInInventory = player.getInventory().getItem(index);

                                if ((!isInsertUpgrade || itemInInventory.getItem() != Items.AIR) && itemInInventory != stack) {
                                    // Get filters
                                    NonNullList<ItemStack> filter = new ConfiguredItemsInFilterItemHandler(tag).getConfiguredItems();

                                    network.getItemStorageTracker().changed(player, itemInInventory.copy());

                                    if (isInsertUpgrade) {
                                        if(IWhitelistBlacklist.acceptsItem(filter, mode, compare, itemInInventory)) {
                                            if (network.insertItem(itemInInventory, itemInInventory.getCount(), Action.SIMULATE).isEmpty()) {
                                                network.insertItem(itemInInventory, itemInInventory.getCount(), Action.PERFORM);
                                                player.getInventory().setItem(index, ItemStack.EMPTY);
                                            }
                                        }
                                    } else {
                                        if (filter.isEmpty()) return;

                                        for (int k = 0; k < filter.size(); k++) {
                                            if (filter.get(k).getItem() == Items.AIR) continue;
                                            if (k != selectedInventorySlots[j] - 1) continue;

                                            // Get upgrades
                                            List<com.refinedmods.refinedstorage.item.UpgradeItem.Type> upgrades = new ArrayList<>();
                                            if (tag.contains("Inventory_1")) {
                                                ListTag tagList2 = tag.getList("Inventory_1", Tag.TAG_COMPOUND);

                                                for (int l = 0; l < tagList2.size(); l++) {
                                                    String itemId = tagList2.getCompound(l).getString("id");
                                                    if(itemId.equals(RS.ID + ":stack_upgrade")) {
                                                        upgrades.add(com.refinedmods.refinedstorage.item.UpgradeItem.Type.STACK);
                                                    } else if(itemId.equals(RS.ID + ":crafting_upgrade")) {
                                                        upgrades.add(com.refinedmods.refinedstorage.item.UpgradeItem.Type.CRAFTING);
                                                    }
                                                }
                                            }

                                            int stackInteractionSize = upgrades.contains(com.refinedmods.refinedstorage.item.UpgradeItem.Type.STACK) ? 64 : 1;

                                            ItemStack requestItemStack = filter.get(k);
                                            StackListEntry<ItemStack> stackEntry = network.getItemStorageCache().getList().getEntry(requestItemStack, compare);
                                            if (stackEntry != null) {
                                                ItemStack item = network.getItemStorageCache().getList().get(stackEntry.getId());

                                                // We copy here because some mods change the NBT tag of an item after getting the stack limit
                                                int maxItemSize = item.getItem().getMaxStackSize(item.copy());

                                                int size = Math.min(stackInteractionSize, maxItemSize);

                                                // Do this before actually extracting, since external storage sends updates as soon as a change happens (so before the storage tracker used to track)
                                                network.getItemStorageTracker().changed(player, item.copy());

                                                ItemStack took = network.extractItem(item, size, Action.SIMULATE);

                                                if (!took.isEmpty()) {
                                                    Optional<IItemHandler> playerInventory = player.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).resolve();
                                                    if (playerInventory.isPresent()) {
                                                        ItemStack remainder = playerInventory.get().insertItem(index, took, true);
                                                        if (remainder.getCount() != took.getCount()) {
                                                            ItemStack inserted = network.extractItem(item, size - remainder.getCount(), Action.PERFORM);
                                                            playerInventory.get().insertItem(index, inserted, false);
                                                            took.setCount(remainder.getCount());
                                                        }

                                                        if (!took.isEmpty() && rsInsertExportUpgrade$insertItemStacked(playerInventory.get(), index, took, true).isEmpty()) {
                                                            took = network.extractItem(item, size, Action.PERFORM);

                                                            rsInsertExportUpgrade$insertItemStacked(playerInventory.get(), index, took, false);
                                                        }
                                                    }
                                                }
                                            } else {
                                                //Try requesting item for auto crafting
                                                if(upgrades.contains(com.refinedmods.refinedstorage.item.UpgradeItem.Type.CRAFTING)) {
                                                    if(itemInInventory.getMaxStackSize() > itemInInventory.getCount() && player instanceof ServerPlayer serverPlayer) {
                                                        StackListEntry<ItemStack> craftingEntry = network.getItemStorageCache().getCraftablesList().getEntry(requestItemStack, compare);
                                                        if(craftingEntry == null) continue;

                                                        boolean alreadyRequesting = false;
                                                        for(ICraftingTask task : network.getCraftingManager().getTasks()) {
                                                            if(task.getRequested().getItem().getItem() == requestItemStack.getItem()) {
                                                                alreadyRequesting = true;
                                                                break;
                                                            }
                                                        }
                                                        if(alreadyRequesting) continue;

                                                        int requestSize = Math.min(stackInteractionSize, itemInInventory.getMaxStackSize() - itemInInventory.getCount());
                                                        network.getItemGridHandler().onCraftingRequested(serverPlayer, craftingEntry.getId(), requestSize);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    network.getNetworkItemManager().drainEnergy(player, isInsertUpgrade ? RS.SERVER_CONFIG.getWirelessGrid().getInsertUsage() : RS.SERVER_CONFIG.getWirelessGrid().getExtractUsage());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique
    @NotNull
    public ItemStack rsInsertExportUpgrade$insertItemStacked(IItemHandler inventory, int slotIndex, @NotNull ItemStack stack, boolean simulate) {
        if (inventory == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return rsInsertExportUpgrade$insertItem(inventory, slotIndex, stack, simulate);
        }

        // go through the slots and try to fill up already existing items
        ItemStack slot = inventory.getStackInSlot(slotIndex);
        if (ItemHandlerHelper.canItemStacksStackRelaxed(slot, stack)) {
            stack = inventory.insertItem(slotIndex, stack, simulate);
        }

        // insert remainder into empty slots
        if (!stack.isEmpty()) {
            // find empty slot
            if (inventory.getStackInSlot(slotIndex).isEmpty()) {
                stack = inventory.insertItem(slotIndex, stack, simulate);
            }
        }

        return stack;
    }

    @Unique
    @NotNull
    public ItemStack rsInsertExportUpgrade$insertItem(IItemHandler dest, int slotIndex, @NotNull ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        stack = dest.insertItem(slotIndex, stack, simulate);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return stack;
    }

    @Unique
    public INetwork rsInsertExportUpgrade$getNetwork(MinecraftServer server, ItemStack stack) {
        if (!isValid(stack)) {
            return null;
        }

        ResourceKey<Level> dimension = getDimension(stack);
        if (dimension == null) {
            return null;
        }

        Level nodeLevel = server.getLevel(dimension);
        if (nodeLevel == null) {
            return null;
        }

        BlockPos pos = new BlockPos(getX(stack), getY(stack), getZ(stack));
        if (!nodeLevel.isLoaded(pos)) {
            return null;
        }

        return NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromBlockEntity(nodeLevel.getBlockEntity(pos)));
    }
}
