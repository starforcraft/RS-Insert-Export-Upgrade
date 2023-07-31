package com.ultramega.rsinsertexportupgrade.registry;

import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import com.ultramega.rsinsertexportupgrade.item.UpgradeItem;
import com.ultramega.rsinsertexportupgrade.util.UpgradeType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RSInsertExportUpgrade.MOD_ID);

    public static final RegistryObject<Item> INSERT_UPGRADE = ITEMS.register("insert_upgrade", () -> new UpgradeItem(UpgradeType.INSERT, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EXPORT_UPGRADE = ITEMS.register("export_upgrade", () -> new UpgradeItem(UpgradeType.EXPORT, new Item.Properties().stacksTo(1)));
}
