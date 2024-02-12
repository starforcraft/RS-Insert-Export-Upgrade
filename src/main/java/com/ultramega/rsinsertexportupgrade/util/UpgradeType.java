package com.ultramega.rsinsertexportupgrade.util;

import java.util.Arrays;
import java.util.Optional;

public enum UpgradeType {
    INSERT(0, "insert"),
    EXPORT(1, "export");

    private final int id;
    private final String name;

    UpgradeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Optional<UpgradeType> valueOf(int value) {
        return Arrays.stream(values())
                .filter(legNo -> legNo.id == value)
                .findFirst();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
