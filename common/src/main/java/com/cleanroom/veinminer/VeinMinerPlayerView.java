package com.cleanroom.veinminer;

import com.cleanroom.common.math.BlockVec3i;

public interface VeinMinerPlayerView {
    BlockVec3i getBlockPos();
    boolean isSneaking();
    String getHeldItemId();
    int getRemainingDurability();
    void damageHeldTool(int damage);
    void causeFoodExhaustion(double exhaustion);
}
