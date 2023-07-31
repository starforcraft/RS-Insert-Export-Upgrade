package com.ultramega.rsinsertexportupgrade;

import com.ultramega.rsinsertexportupgrade.network.NetworkHandler;
import com.ultramega.rsinsertexportupgrade.registry.ClientEventHandler;
import com.ultramega.rsinsertexportupgrade.registry.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RSInsertExportUpgrade.MOD_ID)
public class RSInsertExportUpgrade {
    public static final String MOD_ID = "rsinsertexportupgrade";

    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();

    public RSInsertExportUpgrade() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEventHandler::new);

        RegistryHandler.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        RSInsertExportUpgrade.NETWORK_HANDLER.register();
    }
}
