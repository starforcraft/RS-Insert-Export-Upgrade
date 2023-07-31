package com.ultramega.rsinsertexportupgrade.mixin;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.blockentity.grid.WirelessGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
        if(grid instanceof WirelessGrid) {
            graphics.blit(UPGRADE_SLOTS_TEXTURE, x + imageWidth - 34 + 4, y + 84, 0, 0, 30, 46);
        }
    }
}
