package com.cleanroom.gravestones;

import com.cleanroom.common.math.BlockVec3i;
import com.cleanroom.common.model.ContainerSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SoulGraveManager {

    public record DeathResult(
            boolean graveCreated,
            String reason,
            GraveDataRecord graveRecord,
            DeathCompassData compassData,
            SoulGravePlacementResolver.PlacementResult placement
    ) {
        public static DeathResult skipped(String reason) {
            return new DeathResult(false, reason, null, null, null);
        }

        public static DeathResult placed(
                GraveDataRecord graveRecord,
                DeathCompassData compassData,
                SoulGravePlacementResolver.PlacementResult placement
        ) {
            return new DeathResult(true, "SUCCESS", graveRecord, compassData, placement);
        }
    }

    public static class PlayerInventoryContainer {
        // 0-35: main inventory, 36-39: armor, 40: offhand
        private final ContainerSlot[] slots = new ContainerSlot[41];
        private int xpPoints = 0;

        public PlayerInventoryContainer() {
            for (int i = 0; i < 41; i++) {
                slots[i] = ContainerSlot.empty(i);
            }
        }

        public ContainerSlot getSlot(int index) {
            if (index < 0 || index >= 41) return ContainerSlot.empty(index);
            ContainerSlot s = slots[index];
            return s != null ? s : ContainerSlot.empty(index);
        }

        public void setSlot(int index, ContainerSlot item) {
            if (index >= 0 && index < 41) {
                slots[index] = item != null ? item : ContainerSlot.empty(index);
            }
        }

        public boolean isSlotEmpty(int index) {
            return getSlot(index).isEmpty();
        }

        public int getXpPoints() {
            return xpPoints;
        }

        public void setXpPoints(int xpPoints) {
            this.xpPoints = xpPoints;
        }

        public void addXp(int xp) {
            this.xpPoints += xp;
        }

        public List<ContainerSlot> snapshot() {
            List<ContainerSlot> list = new ArrayList<>(41);
            for (int i = 0; i < 41; i++) {
                list.add(getSlot(i));
            }
            return list;
        }

        public void clear() {
            for (int i = 0; i < 41; i++) {
                slots[i] = ContainerSlot.empty(i);
            }
            this.xpPoints = 0;
        }

        public boolean isEmpty() {
            if (xpPoints > 0) return false;
            for (int i = 0; i < 41; i++) {
                if (!isSlotEmpty(i)) return false;
            }
            return true;
        }

        public boolean giveToMainInventory(ContainerSlot item) {
            if (item == null || item.isEmpty()) return true;
            for (int i = 0; i < 36; i++) {
                if (isSlotEmpty(i)) {
                    slots[i] = new ContainerSlot(i, item.itemId(), item.count(), item.maxCount(), item.tagData());
                    return true;
                }
            }
            return false;
        }
    }

    public record RetrievalResult(
            boolean success,
            String reason,
            int restoredXp,
            List<ContainerSlot> droppedOverflow
    ) {
        public static RetrievalResult denied(String reason) {
            return new RetrievalResult(false, reason, 0, Collections.emptyList());
        }

        public static RetrievalResult ok(int restoredXp, List<ContainerSlot> overflow) {
            return new RetrievalResult(true, "SUCCESS", restoredXp, overflow);
        }
    }

    public static DeathResult handlePlayerDeath(
            String ownerUuid,
            String ownerName,
            BlockVec3i deathPos,
            String dimension,
            PlayerInventoryContainer playerInv,
            boolean keepInventoryGamerule,
            int minBuildHeight,
            int maxBuildHeight,
            boolean isInLava,
            String deathCause
    ) {
        if (keepInventoryGamerule) {
            return DeathResult.skipped("KEEP_INVENTORY");
        }

        if (playerInv.isEmpty()) {
            return DeathResult.skipped("EMPTY_INVENTORY");
        }

        SoulGravePlacementResolver.PlacementResult placement = SoulGravePlacementResolver.resolve(
                deathPos, minBuildHeight, maxBuildHeight, isInLava
        );

        List<ContainerSlot> savedItems = playerInv.snapshot();
        int savedXp = playerInv.getXpPoints();

        // Drain player inventory & XP
        playerInv.clear();

        long now = System.currentTimeMillis();
        GraveDataRecord graveRecord = new GraveDataRecord(
                ownerUuid,
                ownerName,
                now,
                deathCause != null ? deathCause : "Slain",
                deathPos,
                placement.gravePos(),
                dimension,
                savedItems,
                savedXp,
                false
        );

        DeathCompassData compassData = new DeathCompassData(
                placement.gravePos(),
                dimension,
                ownerUuid,
                now
        );

        return DeathResult.placed(graveRecord, compassData, placement);
    }

    public static RetrievalResult retrieveGrave(
            GraveDataRecord grave,
            String retrievingPlayerUuid,
            PlayerInventoryContainer targetInv
    ) {
        if (!grave.ownerUuid().equals(retrievingPlayerUuid) && !grave.isLootableByOthers()) {
            return RetrievalResult.denied("NOT_OWNER");
        }

        List<ContainerSlot> overflow = new ArrayList<>();
        List<ContainerSlot> items = grave.items();

        // 1. Auto-equip armor (slots 36 to 39)
        for (int i = 36; i <= 39 && i < items.size(); i++) {
            ContainerSlot armorItem = items.get(i);
            if (armorItem != null && !armorItem.isEmpty()) {
                if (targetInv.isSlotEmpty(i)) {
                    targetInv.setSlot(i, armorItem);
                } else {
                    if (!targetInv.giveToMainInventory(armorItem)) {
                        overflow.add(armorItem);
                    }
                }
            }
        }

        // 2. Auto-equip offhand (slot 40)
        if (items.size() > 40) {
            ContainerSlot offhand = items.get(40);
            if (offhand != null && !offhand.isEmpty()) {
                if (targetInv.isSlotEmpty(40)) {
                    targetInv.setSlot(40, offhand);
                } else {
                    if (!targetInv.giveToMainInventory(offhand)) {
                        overflow.add(offhand);
                    }
                }
            }
        }

        // 3. Restore main inventory (slots 0 to 35)
        for (int i = 0; i < 36 && i < items.size(); i++) {
            ContainerSlot mainItem = items.get(i);
            if (mainItem != null && !mainItem.isEmpty()) {
                if (targetInv.isSlotEmpty(i)) {
                    targetInv.setSlot(i, mainItem);
                } else {
                    if (!targetInv.giveToMainInventory(mainItem)) {
                        overflow.add(mainItem);
                    }
                }
            }
        }

        // 4. Restore experience
        int xp = grave.storedExperience();
        targetInv.addXp(xp);

        return RetrievalResult.ok(xp, overflow);
    }
}
