package com.cleanroom.veinminer;

import com.cleanroom.common.config.JsonConfigProvider;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class VeinMinerConfig {
    public String activationMode = "HOLD"; // "HOLD", "TOGGLE", "SNEAK"
    public int maxBlocks = 64;
    public double maxRadius = 16.0;
    public boolean toolDamage = true;
    public double exhaustionPerBlock = 0.02;
    public boolean teleportDropsToPlayer = true;
    public boolean requireEffectiveTool = true;
    public Set<String> whitelistedTags = new HashSet<>(Set.of(
            "#c:ores",
            "#forge:ores",
            "#minecraft:logs",
            "#minecraft:dirt"
    ));

    public static VeinMinerConfig load(Path configPath) {
        return JsonConfigProvider.loadConfig(configPath, VeinMinerConfig.class, new VeinMinerConfig());
    }

    public boolean save(Path configPath) {
        return JsonConfigProvider.saveConfig(configPath, this);
    }
}
