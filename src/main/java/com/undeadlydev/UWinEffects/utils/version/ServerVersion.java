package com.undeadlydev.UWinEffects.utils.version;

import org.bukkit.Bukkit;

public enum ServerVersion {

    v1_17(17, 1),
    v1_18(18, 2),
    v1_19(19, 4),
    v1_20(20, 6, "ee13f98a43b9c5abffdcc0bb24154460", 4),
    v1_21(21, 8, "98b42190c84edaa346fd96106ee35d6f", 5),
    NEW("???"),
    ;

    private final String name;
    private final int majorVer;
    private final int minorVer;
    private final String mappingsVersion;
    private final int nmsRevision;

    private ServerVersion(String name) {
        this.name = name;
        this.majorVer = 0;
        this.minorVer = 0;
        this.mappingsVersion = null;
        this.nmsRevision = 0;
    }

    private ServerVersion(int majorVer, int minorVer) {
        this(majorVer, minorVer, null, 0);
    }

    private ServerVersion(int majorVer, int minorVer, String mappingsVersion, int nmsRevision) {
        this.name = "1." + majorVer + (minorVer > 0 ? "." + minorVer : "");
        this.majorVer = majorVer;
        this.minorVer = minorVer;
        this.mappingsVersion = mappingsVersion;
        this.nmsRevision = nmsRevision;
    }

    public static ServerVersion detect() {
        String raw = getMinecraftVersion(); // p.ej. "1.21.1"
        for (ServerVersion version : values()) {
            if (raw.startsWith(version.getName())) {
                return version;
            }
        }
        return NEW; // fallback
    }

    public String getName() {
        return name;
    }

    public int getMajorVer() {
        return majorVer;
    }

    public int getMinorVer() {
        return minorVer;
    }

    public String getMappingsVersion() {
        return mappingsVersion;
    }

    public int getNMSRevision() {
        return nmsRevision;
    }

    public static ServerVersion earliest() {
        return values()[0];
    }

    public static ServerVersion byId(int id) {
        if (id == 0) return null;
        for (ServerVersion version : values()) {
            if (id == version.majorVer) {
                return version;
            }
        }
        return null;
    }

    public boolean isAtLeast(ServerVersion version) {
        return this.compareTo(version) >= 0;
    }

    public boolean isNmsSupported() {
        return nmsRevision > 0;
    }

    public String getNmsVersion() {
        return toString() + "_R" + nmsRevision;
    }

    public static String getMinecraftVersion() {
        String rawVersion = Bukkit.getVersion();
        return rawVersion.substring(rawVersion.lastIndexOf(" ") + 1, rawVersion.length() - 1);
    }

    public static ServerVersion latest() {
        return values()[values().length - 2];
    }
}
