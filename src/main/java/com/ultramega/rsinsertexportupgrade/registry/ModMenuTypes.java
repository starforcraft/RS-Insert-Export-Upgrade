package com.ultramega.rsinsertexportupgrade.registry;

import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import com.ultramega.rsinsertexportupgrade.container.UpgradeContainerMenu;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, RSInsertExportUpgrade.MOD_ID);

    public static final RegistryObject<MenuType<UpgradeContainerMenu>> INSERT_UPGRADE = MENU_TYPES.register("insert_upgrade", () ->
            IForgeMenuType.create((windowId, inv, data) -> new UpgradeContainerMenu(UpgradeType.INSERT, inv.player, data == null ? inv.getSelected() : data.readItem(), windowId, data == null ? -1 : data.readInt())));
    public static final RegistryObject<MenuType<UpgradeContainerMenu>> EXPORT_UPGRADE = MENU_TYPES.register("export_upgrade", () ->
            IForgeMenuType.create((windowId, inv, data) -> new UpgradeContainerMenu(UpgradeType.EXPORT, inv.player, data == null ? inv.getSelected() : data.readItem(), windowId, data == null ? -1 : data.readInt())));
}
