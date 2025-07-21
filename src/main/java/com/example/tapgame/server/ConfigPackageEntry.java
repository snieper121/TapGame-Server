package com.example.tapgame.server;

import com.example.tapgame.server.ServerLog;

public abstract class ConfigPackageEntry {

    protected static final ServerLog LOGGER = new ServerLog("ConfigPackageEntry");

    public ConfigPackageEntry() {
    }

    public abstract boolean isAllowed();

    public abstract boolean isDenied();
}
