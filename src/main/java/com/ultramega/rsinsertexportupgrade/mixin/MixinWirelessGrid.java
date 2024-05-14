package com.ultramega.rsinsertexportupgrade.mixin;

import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.blockentity.grid.WirelessGrid;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.ultramega.rsinsertexportupgrade.item.UpgradeItemHandler;
import com.ultramega.rsinsertexportupgrade.util.IGridUpgrade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(WirelessGrid.class)
public abstract class MixinWirelessGrid implements IGridUpgrade {
    @Unique
    private List<IFilter> rsInsertExportUpgrade$filter;
    @Unique
    private UpgradeItemHandler rsInsertExportUpgrade$upgrade;

    @Inject(at = @At("TAIL"), method = "<init>")
    protected void WirelessGridConstructor(ItemStack stack, MinecraftServer server, PlayerSlot playerSlot, CallbackInfo ci) {
        this.rsInsertExportUpgrade$filter = new ArrayList<>();
        this.rsInsertExportUpgrade$upgrade = (UpgradeItemHandler) new UpgradeItemHandler(rsInsertExportUpgrade$filter)
                .addListener((handler, slot, reading) -> {
                    if (!stack.hasTag()) {
                        stack.setTag(new CompoundTag());
                    }

                    StackUtils.writeItems(handler, 1, stack.getTag());
                });

        if (stack.hasTag()) {
            StackUtils.readItems(rsInsertExportUpgrade$upgrade, 1, stack.getTag());
        }
    }

    @Unique
    @Override
    public List<IFilter> rsInsertExportUpgrade$getFilter() {
        return rsInsertExportUpgrade$filter;
    }

    @Unique
    @Override
    public IItemHandlerModifiable rsInsertExportUpgrade$getUpgrade() {
        return rsInsertExportUpgrade$upgrade;
    }
}
