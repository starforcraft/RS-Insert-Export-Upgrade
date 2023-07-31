package com.ultramega.rsinsertexportupgrade.registry;

import com.ultramega.rsinsertexportupgrade.RSInsertExportUpgrade;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RSInsertExportUpgrade.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB_RSINSERTEXPORTUPGRADE = TABS.register(RSInsertExportUpgrade.MOD_ID, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.rsinsertexportupgrade")).icon(() -> new ItemStack(ModItems.INSERT_UPGRADE.get())).displayItems((featureFlags, output) -> {
        output.accept(new ItemStack(ModItems.INSERT_UPGRADE.get()));
        output.accept(new ItemStack(ModItems.EXPORT_UPGRADE.get()));
    }).build());
}