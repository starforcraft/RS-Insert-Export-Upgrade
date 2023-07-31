package com.ultramega.rsinsertexportupgrade.registry;

import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.screen.UpgradeScreen;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandler {
    public ClientEventHandler() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }

    public void init(FMLClientSetupEvent event) {
        MenuScreens.<UpgradeContainerMenu, UpgradeScreen>register(ModMenuTypes.INSERT_UPGRADE.get(), (containerMenu, inventory, title) -> new UpgradeScreen(UpgradeType.INSERT, containerMenu, inventory, title));
        MenuScreens.<UpgradeContainerMenu, UpgradeScreen>register(ModMenuTypes.EXPORT_UPGRADE.get(), (containerMenu, inventory, title) -> new UpgradeScreen(UpgradeType.EXPORT, containerMenu, inventory, title));
    }
}
