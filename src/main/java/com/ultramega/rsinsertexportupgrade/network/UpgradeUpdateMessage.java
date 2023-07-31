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
    private final int mode;
    private final int[] selectedInventorySlots;

    public UpgradeUpdateMessage(int type, int mode, int[] selectedInventorySlots) {
        this.type = type;
        this.mode = mode;
        this.selectedInventorySlots = selectedInventorySlots;
    }

    public static UpgradeUpdateMessage decode(FriendlyByteBuf buf) {
        return new UpgradeUpdateMessage(buf.readInt(), buf.readInt(), buf.readVarIntArray());
    }

    public static void encode(UpgradeUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.type);
        buf.writeInt(message.mode);
        buf.writeVarIntArray(message.selectedInventorySlots);
    }

    public static void handle(UpgradeUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null && player.containerMenu instanceof UpgradeContainerMenu containerMenu) {
            ctx.get().enqueueWork(() -> {
                if(message.type == UpgradeType.INSERT.getId()) {
                    UpgradeItem.setMode(containerMenu.getUpgradeItem(), message.mode);
                }
                UpgradeItem.setSelectedInventorySlots(containerMenu.getUpgradeItem(), message.selectedInventorySlots);
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
