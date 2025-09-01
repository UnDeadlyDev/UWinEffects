package com.undeadlydev.UWinEffects.utils.version;

import com.undeadlydev.UWinEffects.interfaces.CustomNPC;

public class VersionManager {
    public static final String PACKAGE = "com.undeadlydev.UWinEffects";

    private final ServerVersion serverVersion;
    private final boolean useNMS;
    private final CustomNPC module;

    public VersionManager(ServerVersion serverVersion, boolean useNMS) throws ReflectiveOperationException {
        this.serverVersion = serverVersion;
        this.useNMS = useNMS;
        if (useNMS) {
            module = loadModule("CustomNPCImpl");
        } else {
            module = null;
        }
    }

    public CustomNPC getModule(){
        return module;
    }

    private <T> T loadModule(String name) throws ReflectiveOperationException {
        return (T) Class.forName(PACKAGE + "." + serverVersion.getNmsVersion() + "." + name).getConstructor().newInstance();
    }

    public boolean isUsingNMS() {
        return useNMS;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }
}
