package com.ultramega.rsinsertexportupgrade.mixin;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.ultramega.rsinsertexportupgrade.screen.UpgradeSideButton;
import com.ultramega.rsinsertexportupgrade.util.ISideButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(BaseScreen.class)
public abstract class MixinBaseScreen extends AbstractContainerScreen implements ISideButton {
    @Shadow
    private int sideButtonY;
    @Final
    @Shadow
    private List<SideButton> sideButtons = new ArrayList<>();
    @Unique
    private boolean rsInsertExportUpgrade$moveDown = true;

    public MixinBaseScreen(AbstractContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Unique
    @Override
    public void rsInsertExportUpgrade$refreshUpgradeSideButton() {
        int count = 0;
        for (int i = 0; i < sideButtons.size(); i++) {
            if (sideButtons.get(i) instanceof UpgradeSideButton sideButton) {
                this.removeWidget(sideButton);
                this.sideButtonY -= (sideButton.getHeight() + 2) * (count == 1 ? (rsInsertExportUpgrade$moveDown ? 2 : 1) : 1);
                this.sideButtons.remove(sideButton);

                //I have no idea why but this is required for the y-shift
                if (count == 1) rsInsertExportUpgrade$moveDown = false;
                count++;
            }
        }
    }
}
