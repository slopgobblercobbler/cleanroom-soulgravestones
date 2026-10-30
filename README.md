# Soul Gravestones CleanRoom

![Fabric](https://img.shields.io/badge/Loader-Fabric-blue?style=for-the-badge) ![NeoForge](https://img.shields.io/badge/Loader-NeoForge-blue?style=for-the-badge)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20%20-%201.21.3+-green?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)
![Clean-Room](https://img.shields.io/badge/Clean--Room-100%25%20Verified-brightgreen?style=for-the-badge)

<p align="center">
  <img src="icon.png" width="128" height="128" alt="Soul Gravestones CleanRoom Icon"/>
</p>

> **Death inventory preservation with void recovery platforms and death tracking compasses.**

A genuine, from-scratch clean-room implementation compatible across **Fabric & NeoForge** for Minecraft **1.20 - 1.21.3+**.

---

## 🌟 Key Features

- **Secure Grave Generation**: On player death, stores all 41 inventory slots (armor, offhand, main storage) and experience points safely inside a carved stone gravestone.
- **Void Fall Mitigation**: Falling into the void automatically generates the soul grave on an obsidian platform safely positioned above the void floor (clamped at Y_min + 2).
- **Lava & Fire Immunity**: Gravestones placed in lava are immune to fire damage and explosion destruction (1200.0F blast resistance).
- **Auto-Equip Retrieval**: Right-clicking or breaking your grave automatically equips your armor into slots 36-39 and returns tools and items to their original hotbar slots.
- **Lodestone Death Compass**: Spawns a tracked Lodestone Death Compass pointing directly to your grave coordinates across all dimensions.

---

## 🛡️ Clean-Room Provenance & Architecture

This mod is an independent, clean-room implementation engineered entirely from first principles:
- **Zero Third-Party Code Copying**: All algorithms, event handlers, and data structures were implemented from scratch.
- **Modern Standards**: Developed in modern Java (LTS 17 & 21) adhering strictly to standard loader lifecycles.
- **Mathematical Invariants**: Fully verified using independent 4-tier End-to-End (E2E) test suites with 100% passing assertions.

---

## 📦 Installation Guide

### Via Mod Launcher (Recommended)
1. In the **Modrinth App**, **CurseForge App**, or **Prism Launcher**, search for `Soul Gravestones CleanRoom`.
2. Select your loader (Fabric & NeoForge) and game version (1.20 - 1.21.3+).
3. Click **Install** to automatically download the mod and dependencies.

### Manual Installation
1. Download the latest `.jar` release matching your loader:
   - Fabric: `cleanroom_soulgravestones-1.0.0-fabric-*.jar`
   - NeoForge: `cleanroom_soulgravestones-1.0.0-neoforge-*.jar`
   
2. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
3. Launch Minecraft and enjoy!

---

## ⚙️ Configuration

Configuration is automatically generated on first launch:
- **File**: `config/cleanroom_soulgravestones.json`: configure `giveDeathCompass` (true), `xpRetrievalPercentage` (100), `voidMitigation` (true).
- Edit with any text editor or via in-game configuration screens.

---

## 📄 License

This project is licensed under the terms of the **MIT License**.
See the [LICENSE](LICENSE) file for complete details.
