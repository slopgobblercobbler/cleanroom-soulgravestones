package com.cleanroom.gravestones;

import com.cleanroom.common.math.BlockVec3i;
import com.cleanroom.common.model.ContainerSlot;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SoulGravestonesTest {

    @Test
    void testNormalDeathGeneratesGraveAndDrainsPlayer() {
        SoulGraveManager.PlayerInventoryContainer playerInv = new SoulGraveManager.PlayerInventoryContainer();
        playerInv.setSlot(0, new ContainerSlot(0, "minecraft:diamond_sword", 1, 1, Collections.emptyMap()));
        playerInv.setSlot(38, new ContainerSlot(38, "minecraft:iron_chestplate", 1, 1, Collections.emptyMap()));
        playerInv.setXpPoints(500);

        BlockVec3i deathPos = new BlockVec3i(10, 64, 10);

        SoulGraveManager.DeathResult result = SoulGraveManager.handlePlayerDeath(
                "player-uuid-1",
                "Steve",
                deathPos,
                "minecraft:overworld",
                playerInv,
                false,
                -64,
                320,
                false,
                "Fell from a high place"
        );

        assertTrue(result.graveCreated());
        assertEquals("SUCCESS", result.reason());
        assertNotNull(result.graveRecord());
        assertEquals(deathPos, result.graveRecord().gravePos());
        assertEquals(500, result.graveRecord().storedExperience());

        // Player must be completely drained
        assertTrue(playerInv.isEmpty());
        assertEquals(0, playerInv.getXpPoints());

        // Death tracking compass
        assertNotNull(result.compassData());
        assertEquals(deathPos, result.compassData().lodestonePos());
        assertEquals("minecraft:overworld", result.compassData().dimension());
    }

    @Test
    void testVoidDeathMitigationPlatform() {
        SoulGraveManager.PlayerInventoryContainer playerInv = new SoulGraveManager.PlayerInventoryContainer();
        playerInv.setSlot(0, new ContainerSlot(0, "minecraft:elytra", 1, 1, Collections.emptyMap()));

        BlockVec3i voidDeathPos = new BlockVec3i(0, -80, 0); // below minBuildHeight = -64

        SoulGraveManager.DeathResult result = SoulGraveManager.handlePlayerDeath(
                "player-uuid-1",
                "Steve",
                voidDeathPos,
                "minecraft:overworld",
                playerInv,
                false,
                -64,
                320,
                false,
                "Fell out of the world"
        );

        assertTrue(result.graveCreated());
        // Clamped to minBuildHeight + 2 = -62
        assertEquals(new BlockVec3i(0, -62, 0), result.placement().gravePos());
        assertTrue(result.placement().requiresSupportPlatform());
        // Platform placed at minBuildHeight + 1 = -63
        assertEquals(new BlockVec3i(0, -63, 0), result.placement().platformPos());
        assertEquals("minecraft:obsidian", result.placement().platformBlockId());
    }

    @Test
    void testLavaLakeGravePlacement() {
        SoulGraveManager.PlayerInventoryContainer playerInv = new SoulGraveManager.PlayerInventoryContainer();
        playerInv.setSlot(0, new ContainerSlot(0, "minecraft:netherite_ingot", 5, 64, Collections.emptyMap()));

        BlockVec3i lavaDeathPos = new BlockVec3i(100, 31, -200);

        SoulGraveManager.DeathResult result = SoulGraveManager.handlePlayerDeath(
                "player-uuid-1",
                "Steve",
                lavaDeathPos,
                "minecraft:the_nether",
                playerInv,
                false,
                0,
                256,
                true,
                "Tried to swim in lava"
        );

        assertTrue(result.graveCreated());
        assertTrue(result.placement().isFireproof());
        assertEquals(1200.0f, result.placement().blastResistance(), 1E-5);
    }

    @Test
    void testAutoEquipSoulRetrieval() {
        SoulGraveManager.PlayerInventoryContainer playerInv = new SoulGraveManager.PlayerInventoryContainer();
        playerInv.setSlot(0, new ContainerSlot(0, "minecraft:golden_apple", 5, 64, Collections.emptyMap()));
        playerInv.setSlot(38, new ContainerSlot(38, "minecraft:netherite_chestplate", 1, 1, Collections.emptyMap()));
        playerInv.setSlot(40, new ContainerSlot(40, "minecraft:shield", 1, 1, Collections.emptyMap()));
        playerInv.setXpPoints(350);

        SoulGraveManager.DeathResult death = SoulGraveManager.handlePlayerDeath(
                "player-uuid-1",
                "Steve",
                new BlockVec3i(15, 64, 15),
                "minecraft:overworld",
                playerInv,
                false,
                -64,
                320,
                false,
                "Slain by Zombie"
        );

        // Respawned empty player
        SoulGraveManager.PlayerInventoryContainer respawnInv = new SoulGraveManager.PlayerInventoryContainer();

        SoulGraveManager.RetrievalResult retrieval = SoulGraveManager.retrieveGrave(
                death.graveRecord(),
                "player-uuid-1",
                respawnInv
        );

        assertTrue(retrieval.success());
        assertEquals(350, retrieval.restoredXp());
        assertEquals(350, respawnInv.getXpPoints());

        // Armor slot 38 auto-equipped
        assertFalse(respawnInv.isSlotEmpty(38));
        assertEquals("minecraft:netherite_chestplate", respawnInv.getSlot(38).itemId());

        // Offhand slot 40 auto-equipped
        assertFalse(respawnInv.isSlotEmpty(40));
        assertEquals("minecraft:shield", respawnInv.getSlot(40).itemId());

        // Main slot 0 restored
        assertFalse(respawnInv.isSlotEmpty(0));
        assertEquals("minecraft:golden_apple", respawnInv.getSlot(0).itemId());
        assertEquals(5, respawnInv.getSlot(0).count());
    }

    @Test
    void testEmptyInventoryDeathPlacesNoGrave() {
        SoulGraveManager.PlayerInventoryContainer playerInv = new SoulGraveManager.PlayerInventoryContainer();

        SoulGraveManager.DeathResult result = SoulGraveManager.handlePlayerDeath(
                "player-uuid-1",
                "Steve",
                new BlockVec3i(0, 64, 0),
                "minecraft:overworld",
                playerInv,
                false,
                -64,
                320,
                false,
                "Starved"
        );

        assertFalse(result.graveCreated());
        assertEquals("EMPTY_INVENTORY", result.reason());
    }

    @Test
    void testKeepInventoryGamerulePlacesNoGrave() {
        SoulGraveManager.PlayerInventoryContainer playerInv = new SoulGraveManager.PlayerInventoryContainer();
        playerInv.setSlot(0, new ContainerSlot(0, "minecraft:diamond", 64, 64, Collections.emptyMap()));

        SoulGraveManager.DeathResult result = SoulGraveManager.handlePlayerDeath(
                "player-uuid-1",
                "Steve",
                new BlockVec3i(0, 64, 0),
                "minecraft:overworld",
                playerInv,
                true, // keepInventory = true
                -64,
                320,
                false,
                "Drowned"
        );

        assertFalse(result.graveCreated());
        assertEquals("KEEP_INVENTORY", result.reason());
        // Items retained in player inventory
        assertFalse(playerInv.isEmpty());
    }

    @Test
    void testNonOwnerRetrievalRejected() {
        SoulGraveManager.PlayerInventoryContainer playerInv = new SoulGraveManager.PlayerInventoryContainer();
        playerInv.setSlot(0, new ContainerSlot(0, "minecraft:diamond", 1, 64, Collections.emptyMap()));

        SoulGraveManager.DeathResult death = SoulGraveManager.handlePlayerDeath(
                "owner-uuid",
                "OwnerPlayer",
                new BlockVec3i(0, 64, 0),
                "minecraft:overworld",
                playerInv,
                false,
                -64,
                320,
                false,
                "Slain"
        );

        SoulGraveManager.PlayerInventoryContainer thiefInv = new SoulGraveManager.PlayerInventoryContainer();
        SoulGraveManager.RetrievalResult retrieval = SoulGraveManager.retrieveGrave(
                death.graveRecord(),
                "thief-uuid",
                thiefInv
        );

        assertFalse(retrieval.success());
        assertEquals("NOT_OWNER", retrieval.reason());
        assertTrue(thiefInv.isEmpty());
    }
}
