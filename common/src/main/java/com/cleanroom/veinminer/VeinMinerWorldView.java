package com.cleanroom.veinminer;

import com.cleanroom.common.math.BlockVec3i;
import java.util.List;
import java.util.Set;

public interface VeinMinerWorldView {
    String getBlockId(BlockVec3i pos);
    Set<String> getBlockTags(BlockVec3i pos);
    boolean isTagMatch(BlockVec3i pos, Set<String> validTags);
    boolean setBlockToAir(BlockVec3i pos);
    List<String> getDrops(BlockVec3i pos);
    void spawnDroppedItem(BlockVec3i pos, String itemId);
}
