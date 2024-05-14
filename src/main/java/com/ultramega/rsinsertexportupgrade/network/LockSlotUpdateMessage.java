package com.ultramega.rsinsertexportupgrade.network;

import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.container.UpgradePlayerSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LockSlotUpdateMessage {
    private final int slotId;
    private final boolean cancelPickup;

    public LockSlotUpdateMessage(int slotId, boolean cancelPickup) {
        this.slotId = slotId;
        this.cancelPickup = cancelPickup;
    }

    public static LockSlotUpdateMessage decode(FriendlyByteBuf buf) {
        return new LockSlotUpdateMessage(buf.readInt(), buf.readBoolean());
    }

    public static void encode(LockSlotUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.slotId);
        buf.writeBoolean(message.cancelPickup);
    }

    public static void handle(LockSlotUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null && player.containerMenu instanceof UpgradeContainerMenu) {
            ctx.get().enqueueWork(() -> {
                if(player.containerMenu.slots.get(message.slotId) instanceof UpgradePlayerSlot playerSlot) {
                    playerSlot.setCancelPickup(message.cancelPickup);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
