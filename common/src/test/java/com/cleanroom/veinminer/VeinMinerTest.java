package com.cleanroom.veinminer;

import com.cleanroom.common.math.BlockVec3i;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class VeinMinerTest {

    private static class MockVeinWorld implements VeinMinerWorldView {
        private final Map<BlockVec3i, String> blocks = new HashMap<>();
        private final Map<BlockVec3i, Set<String>> tags = new HashMap<>();
        private final List<Map.Entry<BlockVec3i, String>> droppedItems = new ArrayList<>();

        public void setBlock(BlockVec3i pos, String blockId, Set<String> blockTags) {
            blocks.put(pos, blockId);
            tags.put(pos, blockTags != null ? new HashSet<>(blockTags) : new HashSet<>());
        }

        @Override
        public String getBlockId(BlockVec3i pos) {
            return blocks.getOrDefault(pos, "minecraft:air");
        }

        @Override
        public Set<String> getBlockTags(BlockVec3i pos) {
            return tags.getOrDefault(pos, Collections.emptySet());
        }

        @Override
        public boolean isTagMatch(BlockVec3i pos, Set<String> validTags) {
            Set<String> blockTags = getBlockTags(pos);
            for (String vt : validTags) {
                if (blockTags.contains(vt)) return true;
            }
            return false;
        }

        @Override
        public boolean setBlockToAir(BlockVec3i pos) {
            return blocks.remove(pos) != null;
        }

        @Override
        public List<String> getDrops(BlockVec3i pos) {
            String id = getBlockId(pos);
            if ("minecraft:air".equals(id)) return Collections.emptyList();
            return List.of(id);
        }

        @Override
        public void spawnDroppedItem(BlockVec3i pos, String itemId) {
            droppedItems.add(Map.entry(pos, itemId));
        }
    }

    private static class MockVeinPlayer implements VeinMinerPlayerView {
        BlockVec3i pos = new BlockVec3i(0, 64, 0);
        boolean sneaking = false;
        String heldTool = "minecraft:diamond_pickaxe";
        int durability = 0;
        int maxDurability = 1561;
        double foodExhaustion = 0.0;

        @Override
        public BlockVec3i getBlockPos() { return pos; }

        @Override
        public boolean isSneaking() { return sneaking; }

        @Override
        public String getHeldItemId() { return heldTool; }

        @Override
        public int getRemainingDurability() { return Math.max(0, maxDurability - durability); }

        @Override
        public void damageHeldTool(int damage) {
            durability += damage;
            if (durability >= maxDurability) {
                heldTool = null;
            }
        }

        @Override
        public void causeFoodExhaustion(double exhaustion) {
            foodExhaustion += exhaustion;
        }
    }

    private MockVeinWorld world;
    private MockVeinPlayer player;
    private VeinMinerEngine engine;

    @BeforeEach
    void setUp() {
        world = new MockVeinWorld();
        player = new MockVeinPlayer();
        engine = new VeinMinerEngine();
    }

    private BlockVec3i buildOreVein(int count) {
        BlockVec3i origin = new BlockVec3i(10, 20, 10);
        Set<String> oreTags = Set.of("#c:ores", "#forge:ores");
        for (int i = 0; i < count; i++) {
            world.setBlock(new BlockVec3i(10 + i, 20, 10), "minecraft:iron_ore", oreTags);
        }
        return origin;
    }

    @Test
    void testExcavateContiguousIronOreVein() {
        BlockVec3i origin = buildOreVein(6);
        VeinMinerResult res = engine.mineVein(world, player, origin, true);

        assertTrue(res.success());
        assertEquals(6, res.blocksMined());
        assertEquals(6, res.damageDealt());
        assertEquals(0.12, res.exhaustionAdded(), 1e-4);
        assertEquals(6, player.durability);

        // Verify all 6 blocks removed from world
        for (int i = 0; i < 6; i++) {
            assertEquals("minecraft:air", world.getBlockId(new BlockVec3i(10 + i, 20, 10)));
        }
    }

    @Test
    void testDropTeleportationAggregatesAtPlayer() {
        player.pos = new BlockVec3i(50, 64, 50);
        BlockVec3i origin = buildOreVein(4);

        VeinMinerResult res = engine.mineVein(world, player, origin, true);
        assertTrue(res.success());
        assertEquals(4, world.droppedItems.size());

        // All drops spawned at player position
        for (Map.Entry<BlockVec3i, String> drop : world.droppedItems) {
            assertEquals(new BlockVec3i(50, 64, 50), drop.getKey());
        }
    }

    @Test
    void testInactiveModeMinesSingleBlock() {
        BlockVec3i origin = buildOreVein(4);
        VeinMinerResult res = engine.mineVein(world, player, origin, false);

        assertFalse(res.success());
        assertEquals("INACTIVE", res.reason());
        assertEquals(1, res.blocksMined());

        // Remaining blocks still exist in world
        assertEquals("minecraft:iron_ore", world.getBlockId(new BlockVec3i(11, 20, 10)));
    }

    @Test
    void testNonMinableTagRejected() {
        BlockVec3i pos = new BlockVec3i(5, 60, 5);
        world.setBlock(pos, "minecraft:obsidian", Collections.emptySet()); // not in tags

        VeinMinerResult res = engine.mineVein(world, player, pos, true);
        assertFalse(res.success());
        assertEquals("NOT_MINABLE_TAG", res.reason());
        assertEquals(1, res.blocksMined());
    }

    @Test
    void testDurabilityTruncation() {
        BlockVec3i origin = buildOreVein(8);
        player.durability = 128;
        player.maxDurability = 131; // Only 3 uses remaining

        VeinMinerResult res = engine.mineVein(world, player, origin, true);
        assertEquals(3, res.blocksMined());
        assertNull(player.heldTool); // Tool broke!

        // 4th block onwards still intact
        assertEquals("minecraft:iron_ore", world.getBlockId(new BlockVec3i(13, 20, 10)));
    }

    @Test
    void testMaxBlocksLimit() {
        BlockVec3i origin = new BlockVec3i(0, 20, 0);
        Set<String> oreTags = Set.of("#c:ores");
        for (int i = 0; i < 100; i++) {
            world.setBlock(new BlockVec3i(i % 10, 20 + (i / 10), 0), "minecraft:copper_ore", oreTags);
        }

        VeinMinerResult res = engine.mineVein(world, player, origin, true);
        assertTrue(res.success());
        assertEquals(64, res.blocksMined()); // Capped at 64
        assertEquals(64, res.damageDealt());
    }

    @Test
    void testMaxRadiusLimit() {
        BlockVec3i origin = new BlockVec3i(0, 20, 0);
        Set<String> oreTags = Set.of("#c:ores");
        for (int x = 0; x < 25; x++) {
            world.setBlock(new BlockVec3i(x, 20, 0), "minecraft:iron_ore", oreTags);
        }

        VeinMinerResult res = engine.mineVein(world, player, origin, true);
        assertTrue(res.success());
        // Positions 0 to 16 inclusive = 17 blocks
        assertEquals(17, res.blocksMined());
        assertEquals("minecraft:iron_ore", world.getBlockId(new BlockVec3i(17, 20, 0)));
    }
}
