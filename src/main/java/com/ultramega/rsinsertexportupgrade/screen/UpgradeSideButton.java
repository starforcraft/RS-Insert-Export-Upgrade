package com.ultramega.rsinsertexportupgrade.screen;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import com.ultramega.rsinsertexportupgrade.network.OpenUpgradeUpdateMessage;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

public class UpgradeSideButton extends SideButton {
    private final UpgradeType type;
    private final int selectedSideButton;

    public UpgradeSideButton(BaseScreen<?> screen, UpgradeType type, int selectedSideButton) {
        super(screen);
        this.type = type;
        this.selectedSideButton = selectedSideButton;
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.rsinsertexportupgrade.open_upgrade", I18n.get("item.rsinsertexportupgrade." + type.getName() + "_upgrade"));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(new ResourceLocation(RSInsertExportUpgrade.MOD_ID, "textures/item/" + type.getName() + "_upgrade.png"), x, y, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void onPress() {
        RSInsertExportUpgrade.NETWORK_HANDLER.sendToServer(new OpenUpgradeUpdateMessage(type.getId(), selectedSideButton));
    }
}
