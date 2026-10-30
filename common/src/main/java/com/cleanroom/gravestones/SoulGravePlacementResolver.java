package com.cleanroom.gravestones;

import com.cleanroom.common.math.BlockVec3i;

public final class SoulGravePlacementResolver {

    public record PlacementResult(
            BlockVec3i gravePos,
            boolean requiresSupportPlatform,
            BlockVec3i platformPos,
            String platformBlockId,
            boolean isFireproof,
            float blastResistance
    ) {}

    public static PlacementResult resolve(
            BlockVec3i deathPos,
            int minBuildHeight,
            int maxBuildHeight,
            boolean isInLava
    ) {
        int x = deathPos.x();
        int y = deathPos.y();
        int z = deathPos.z();

        boolean voidFall = y < minBuildHeight;
        boolean requiresPlatform = false;
        BlockVec3i platformPos = null;
        String platformBlock = null;

        if (voidFall) {
            // Void death mitigation: clamp grave at minBuildHeight + 2
            y = minBuildHeight + 2;
            requiresPlatform = true;
            // Obsidian platform directly below grave at minBuildHeight + 1
            platformPos = new BlockVec3i(x, minBuildHeight + 1, z);
            platformBlock = "minecraft:obsidian";
        } else if (y >= maxBuildHeight) {
            y = maxBuildHeight - 2;
        }

        BlockVec3i gravePos = new BlockVec3i(x, y, z);
        boolean fireproof = isInLava;
        float blastResistance = 1200.0f; // Obsidian-tier explosion immunity

        return new PlacementResult(
                gravePos,
                requiresPlatform,
                platformPos,
                platformBlock,
                fireproof,
                blastResistance
        );
    }
}
