package com.ultramega.rsinsertexportupgrade.mixin;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.ultramega.rsinsertexportupgrade.util.IGridUpgrade;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GridContainerMenu.class)
public abstract class MixinGridContainerMenu extends BaseContainerMenu {
    @Final
    @Shadow
    private IGrid grid;

    protected MixinGridContainerMenu(@Nullable MenuType<?> type, @Nullable BaseBlockEntity blockEntity, Player player, int windowId) {
        super(type, blockEntity, player, windowId);
    }

    @Inject(at = @At("TAIL"), method = "initSlots")
    public void initSlots(CallbackInfo ci) {
        if(grid instanceof IGridUpgrade wirelessGrid) {
            rsInsertExportUpgrade$addUpgradeSlots(wirelessGrid);
        }
    }

    @Unique
    private void rsInsertExportUpgrade$addUpgradeSlots(IGridUpgrade wirelessGrid) {
        for (int i = 0; i < 2; ++i) {
            addSlot(new SlotItemHandler(wirelessGrid.rsInsertExportUpgrade$getUpgrade(), i, 204, 90 + (18 * i)));
        }

        transferManager.addBiTransfer(getPlayer().getInventory(), wirelessGrid.rsInsertExportUpgrade$getUpgrade());
    }
}
