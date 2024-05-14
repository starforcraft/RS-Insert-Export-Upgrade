package com.ultramega.rsinsertexportupgrade.network;

import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.item.UpgradeItem;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpgradeUpdateMessage {
    private final int type;
    private final int compare;
    private final int mode;
    private final int[] selectedInventorySlots;
    private final int selectedSideButton;

    public UpgradeUpdateMessage(int type, int compare, int mode, int[] selectedInventorySlots, int selectedSideButton) {
        this.type = type;
        this.compare = compare;
        this.mode = mode;
        this.selectedInventorySlots = selectedInventorySlots;
        this.selectedSideButton = selectedSideButton;
    }

    public static UpgradeUpdateMessage decode(FriendlyByteBuf buf) {
        return new UpgradeUpdateMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readVarIntArray(), buf.readInt());
    }

    public static void encode(UpgradeUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.type);
        buf.writeInt(message.compare);
        buf.writeInt(message.mode);
        buf.writeVarIntArray(message.selectedInventorySlots);
        buf.writeInt(message.selectedSideButton);
    }

    public static void handle(UpgradeUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null && player.containerMenu instanceof UpgradeContainerMenu containerMenu) {
            ctx.get().enqueueWork(() -> {
                UpgradeItem.setCompare(containerMenu.getGridItem(), message.compare, message.selectedSideButton);
                UpgradeItem.setSelectedInventorySlots(containerMenu.getGridItem(), message.selectedInventorySlots, message.selectedSideButton);
                UpgradeItem.setMode(containerMenu.getGridItem(), message.mode, message.selectedSideButton);
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
