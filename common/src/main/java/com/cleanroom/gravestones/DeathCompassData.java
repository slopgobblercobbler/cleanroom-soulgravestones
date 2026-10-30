package com.cleanroom.gravestones;

import com.cleanroom.common.math.BlockVec3i;

public record DeathCompassData(
        BlockVec3i lodestonePos,
        String dimension,
        String ownerUuid,
        long createdAtEpochMs
) {
    public double distanceTo(BlockVec3i currentPos) {
        if (currentPos == null || lodestonePos == null) return 0.0;
        return lodestonePos.dist(currentPos);
    }
}
