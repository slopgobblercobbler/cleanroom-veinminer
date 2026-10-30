package com.cleanroom.veinminer;

import java.util.List;

public record VeinMinerResult(
        boolean success,
        int blocksMined,
        int damageDealt,
        double exhaustionAdded,
        String reason,
        List<String> gatheredDrops
) {
    public static VeinMinerResult singleBlock(String reason, double exhaustion, List<String> drops) {
        return new VeinMinerResult(false, 1, 1, exhaustion, reason, drops);
    }
}
