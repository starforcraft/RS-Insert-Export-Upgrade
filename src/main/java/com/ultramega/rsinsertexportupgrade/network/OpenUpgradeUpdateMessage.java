package com.ultramega.rsinsertexportupgrade.network;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.blockentity.grid.WirelessGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OpenUpgradeUpdateMessage {
    private final int type;
    private final int selectedSideButton;

    public OpenUpgradeUpdateMessage(int type, int selectedSideButton) {
        this.type = type;
        this.selectedSideButton = selectedSideButton;
    }

    public static OpenUpgradeUpdateMessage decode(FriendlyByteBuf buf) {
        return new OpenUpgradeUpdateMessage(buf.readInt(), buf.readInt());
    }

    public static void encode(OpenUpgradeUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.type);
        buf.writeInt(message.selectedSideButton);
    }

    public static void handle(OpenUpgradeUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            ctx.get().enqueueWork(() -> {
                if (player.containerMenu instanceof GridContainerMenu) {
                    IGrid grid = ((GridContainerMenu) player.containerMenu).getGrid();
                    if (grid instanceof WirelessGrid) {
                        ItemStack stack = ((WirelessGrid) grid).getStack();

                        NetworkHooks.openScreen(player, new MenuProvider() {
                            @NotNull
                            @Override
                            public Component getDisplayName() {
                                return Component.translatable("item.rsinsertexportupgrade." + UpgradeType.valueOf(message.type).get().getName() + "_upgrade");
                            }

                            @Override
                            public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
                                return new UpgradeContainerMenu(UpgradeType.valueOf(message.type).get(), player, stack, windowId, message.selectedSideButton);
                            }
                        }, buf -> {
                            buf.writeItem(stack);
                            buf.writeInt(message.selectedSideButton);
                        });
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
