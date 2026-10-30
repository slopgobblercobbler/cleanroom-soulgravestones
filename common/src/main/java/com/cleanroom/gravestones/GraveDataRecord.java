package com.cleanroom.gravestones;

import com.cleanroom.common.math.BlockVec3i;
import com.cleanroom.common.model.ContainerSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record GraveDataRecord(
        String ownerUuid,
        String ownerName,
        long deathTimeEpochMs,
        String deathCauseMessage,
        BlockVec3i deathPos,
        BlockVec3i gravePos,
        String dimension,
        List<ContainerSlot> items,
        int storedExperience,
        boolean isLootableByOthers
) {
    public GraveDataRecord {
        items = items != null ? Collections.unmodifiableList(new ArrayList<>(items)) : Collections.emptyList();
    }

    public boolean hasItems() {
        if (items.isEmpty()) return false;
        for (ContainerSlot s : items) {
            if (s != null && !s.isEmpty()) return true;
        }
        return false;
    }
}
