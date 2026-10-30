package com.cleanroom.veinminer.fabric;

import com.cleanroom.veinminer.VeinMinerCore;

public class VeinMinerFabric {
    private final VeinMinerCore core = new VeinMinerCore();

    public void onInitialize() {
        System.out.println("[CleanRoom Fabric] Bootstrapping " + VeinMinerCore.MOD_NAME);
        core.onInitializeCommon();
    }
}
