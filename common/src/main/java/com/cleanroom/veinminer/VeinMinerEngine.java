package com.cleanroom.veinminer;

import com.cleanroom.common.math.BlockVec3i;

import java.util.*;

public class VeinMinerEngine {
    private final VeinMinerConfig config;

    public VeinMinerEngine(VeinMinerConfig config) {
        this.config = config;
    }

    public VeinMinerEngine() {
        this(new VeinMinerConfig());
    }

    public VeinMinerConfig getConfig() {
        return config;
    }

    public VeinMinerResult mineVein(
            VeinMinerWorldView world,
            VeinMinerPlayerView player,
            BlockVec3i originPos,
            boolean isActive
    ) {
        // If inactive, break single block
        if (!isActive) {
            List<String> drops = world.getDrops(originPos);
            world.setBlockToAir(originPos);
            for (String d : drops) {
                world.spawnDroppedItem(originPos, d);
            }
            player.damageHeldTool(1);
            player.causeFoodExhaustion(config.exhaustionPerBlock);
            return VeinMinerResult.singleBlock("INACTIVE", config.exhaustionPerBlock, drops);
        }

        // Tag whitelist verification
        if (!world.isTagMatch(originPos, config.whitelistedTags)) {
            List<String> drops = world.getDrops(originPos);
            world.setBlockToAir(originPos);
            for (String d : drops) {
                world.spawnDroppedItem(originPos, d);
            }
            player.damageHeldTool(1);
            player.causeFoodExhaustion(config.exhaustionPerBlock);
            return VeinMinerResult.singleBlock("NOT_MINABLE_TAG", config.exhaustionPerBlock, drops);
        }

        String targetBlockId = world.getBlockId(originPos);

        // 3D Flood Fill (BFS)
        List<BlockVec3i> discoveredVein = new ArrayList<>();
        Queue<BlockVec3i> queue = new ArrayDeque<>();
        Set<BlockVec3i> visited = new HashSet<>();

        queue.add(originPos);
        visited.add(originPos);
        discoveredVein.add(originPos);

        double maxRadiusSq = config.maxRadius * config.maxRadius;

        while (!queue.isEmpty() && discoveredVein.size() < config.maxBlocks) {
            BlockVec3i curr = queue.poll();
            for (BlockVec3i neighbor : curr.getNeighbors26()) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);

                if (originPos.distSq(neighbor) > maxRadiusSq) {
                    continue;
                }

                if (world.getBlockId(neighbor).equals(targetBlockId)) {
                    discoveredVein.add(neighbor);
                    queue.add(neighbor);
                    if (discoveredVein.size() >= config.maxBlocks) {
                        break;
                    }
                }
            }
        }

        // Durability bounds
        int remDurability = player.getRemainingDurability();
        int blocksToMineCount = discoveredVein.size();
        if (remDurability > 0 && remDurability < blocksToMineCount) {
            blocksToMineCount = remDurability;
        }

        List<BlockVec3i> blocksToMine = discoveredVein.subList(0, blocksToMineCount);
        List<String> gatheredDrops = new ArrayList<>();

        for (BlockVec3i pos : blocksToMine) {
            List<String> drops = world.getDrops(pos);
            gatheredDrops.addAll(drops);
            world.setBlockToAir(pos);
        }

        // Apply tool damage and food exhaustion
        player.damageHeldTool(blocksToMineCount);
        double totalExhaustion = blocksToMineCount * config.exhaustionPerBlock;
        player.causeFoodExhaustion(totalExhaustion);

        // Drop aggregation: teleport loot to player position
        BlockVec3i dropSpawnTarget = config.teleportDropsToPlayer ? player.getBlockPos() : originPos;
        for (String drop : gatheredDrops) {
            world.spawnDroppedItem(dropSpawnTarget, drop);
        }

        return new VeinMinerResult(
                true,
                blocksToMineCount,
                blocksToMineCount,
                totalExhaustion,
                "SUCCESS",
                gatheredDrops
        );
    }
}
