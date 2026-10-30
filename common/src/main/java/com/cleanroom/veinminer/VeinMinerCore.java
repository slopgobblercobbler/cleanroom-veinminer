package com.cleanroom.veinminer;

import com.cleanroom.common.api.CommonModInitializer;
import com.cleanroom.common.event.BlockBreakEvent;
import com.cleanroom.common.event.CommonEventBus;
import com.cleanroom.common.telemetry.CleanRoomTelemetry;

public class VeinMinerCore implements CommonModInitializer {
    public static final String MOD_ID = "cleanroom_veinminer";
    public static final String MOD_NAME = "VeinMiner CleanRoom";

    private static VeinMinerCore instance;
    private final VeinMinerConfig config = new VeinMinerConfig();
    private final VeinMinerEngine engine = new VeinMinerEngine(config);

    public static VeinMinerCore getInstance() {
        return instance;
    }

    public VeinMinerConfig getConfig() {
        return config;
    }

    public VeinMinerEngine getEngine() {
        return engine;
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public void onInitializeCommon() {
        instance = this;
        System.out.println("[CleanRoom] Initialized core logic for " + MOD_NAME + " (" + MOD_ID + ")");
        CleanRoomTelemetry.record("INIT", "VeinMiner initialized with maxBlocks=" + config.maxBlocks);

        CommonEventBus.getInstance().register(BlockBreakEvent.class, event -> {
            CleanRoomTelemetry.record("VEINMINER_BREAK", "VeinMiner break check at " + event.getPos());
        });
    }
}
