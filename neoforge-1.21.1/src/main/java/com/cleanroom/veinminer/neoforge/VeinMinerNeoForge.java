package com.cleanroom.veinminer.neoforge;

import com.cleanroom.veinminer.VeinMinerCore;

public class VeinMinerNeoForge {
    private final VeinMinerCore core = new VeinMinerCore();

    public VeinMinerNeoForge() {
        System.out.println("[CleanRoom NeoForge] Bootstrapping " + VeinMinerCore.MOD_NAME);
        core.onInitializeCommon();
    }
}
