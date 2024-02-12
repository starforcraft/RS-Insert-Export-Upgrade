package com.ultramega.rsinsertexportupgrade.mixin;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import com.ultramega.rsinsertexportupgrade.item.UpgradeItemHandler;
import com.ultramega.rsinsertexportupgrade.registry.ModItems;
import com.ultramega.rsinsertexportupgrade.screen.UpgradeSideButton;
import com.ultramega.rsinsertexportupgrade.util.IGridUpgrade;
import com.ultramega.rsinsertexportupgrade.util.ISideButton;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GridScreen.class)
public abstract class MixinGridScreen extends BaseScreen {
    @Unique
    @Final
    private final ResourceLocation UPGRADE_SLOTS_TEXTURE = new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/gui/upgrade_slots.png");
    @Shadow
    @Final
    private IGrid grid;

    protected MixinGridScreen(AbstractContainerMenu containerMenu, int xSize, int ySize, Inventory inventory, Component title) {
        super(containerMenu, xSize, ySize, inventory, title);
    }

    @Inject(at = @At("TAIL"), method = "renderBackground")
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        if (grid instanceof IGridUpgrade) {
            graphics.blit(UPGRADE_SLOTS_TEXTURE, x + imageWidth - 34 + 4, y + 84, 0, 0, 30, 46, 30, 46);
        }
    }

    @Inject(at = @At("TAIL"), method = "onPostInit")
    public void onPostInit(int x, int y, CallbackInfo ci) {
        rsInsertExportUpgrade$addUpgradeSideButton();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(int x, int y, CallbackInfo ci) {
        ((ISideButton) this).rsInsertExportUpgrade$refreshUpgradeSideButton();
        rsInsertExportUpgrade$addUpgradeSideButton();
    }

    @Unique
    public void rsInsertExportUpgrade$addUpgradeSideButton() {
        if (grid instanceof IGridUpgrade) {
            for (int i = 0; i < getMenu().slots.size(); i++) {
                if (getMenu().getSlot(i) instanceof SlotItemHandler itemHandler && itemHandler.getItemHandler() instanceof UpgradeItemHandler) {
                    if (itemHandler.getItem().getItem() == ModItems.INSERT_UPGRADE.get()) {
                        ((ISideButton) this).rsInsertExportUpgrade$addSideButton(new UpgradeSideButton(this, UpgradeType.INSERT, itemHandler.getSlotIndex()));
                    } else if (itemHandler.getItem().getItem() == ModItems.EXPORT_UPGRADE.get()) {
                        ((ISideButton) this).rsInsertExportUpgrade$addSideButton(new UpgradeSideButton(this, UpgradeType.EXPORT, itemHandler.getSlotIndex()));
                    }
                }
            }
        }
    }
}
