package com.ultramega.rsinsertexportupgrade.util;

import com.refinedmods.refinedstorage.api.util.IFilter;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;

public interface IGridUpgrade {
    List<IFilter> rsInsertExportUpgrade$getUpgrades();
    IItemHandlerModifiable rsInsertExportUpgrade$getUpgrade();
}
