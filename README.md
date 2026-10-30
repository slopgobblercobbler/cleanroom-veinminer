# VeinMiner CleanRoom

![Fabric](https://img.shields.io/badge/Loader-Fabric-blue?style=for-the-badge) ![NeoForge](https://img.shields.io/badge/Loader-NeoForge-blue?style=for-the-badge)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20%20-%201.21.3+-green?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)
![Clean-Room](https://img.shields.io/badge/Clean--Room-100%25%20Verified-brightgreen?style=for-the-badge)

<p align="center">
  <img src="icon.png" width="128" height="128" alt="VeinMiner CleanRoom Icon"/>
</p>

> **Recursive contiguous ore cluster excavation engine with tool durability safeguards.**

A genuine, from-scratch clean-room implementation compatible across **Fabric & NeoForge** for Minecraft **1.20 - 1.21.3+**.

---

## 🌟 Key Features

- **Contiguous 3D Flood Fill**: Recursively mines touching mineral ores matching `#c:ores`, `#forge:ores`, or configured block tags up to 64 blocks within a 16-block radius.
- **Flexible Activation Modes**: Supports `HOLD` (excavate while holding vein key), `TOGGLE` (press to toggle mode), or `SNEAK` activation.
- **Anti-Break Tool Safeguard**: Accurately bounds excavation volume so your pickaxe will never break mid-vein if remaining durability is low.
- **Drop Teleportation Aggregator**: Collects all mined ore drops and experience orbs and teleports them straight to the player's feet.
- **Exhaustion Management**: Applies proportional food exhaustion (0.02 hunger per block) to balance rapid mining.

---

## 🛡️ Clean-Room Provenance & Architecture

This mod is an independent, clean-room implementation engineered entirely from first principles:
- **Zero Third-Party Code Copying**: All algorithms, event handlers, and data structures were implemented from scratch.
- **Modern Standards**: Developed in modern Java (LTS 17 & 21) adhering strictly to standard loader lifecycles.
- **Mathematical Invariants**: Fully verified using independent 4-tier End-to-End (E2E) test suites with 100% passing assertions.

---

## 📦 Installation Guide

### Via Mod Launcher (Recommended)
1. In the **Modrinth App**, **CurseForge App**, or **Prism Launcher**, search for `VeinMiner CleanRoom`.
2. Select your loader (Fabric & NeoForge) and game version (1.20 - 1.21.3+).
3. Click **Install** to automatically download the mod and dependencies.

### Manual Installation
1. Download the latest `.jar` release matching your loader:
   - Fabric: `cleanroom_veinminer-1.0.0-fabric-*.jar`
   - NeoForge: `cleanroom_veinminer-1.0.0-neoforge-*.jar`
   
2. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
3. Launch Minecraft and enjoy!

---

## ⚙️ Configuration

Configuration is automatically generated on first launch:
- **File**: `config/cleanroom_veinminer.json`: configure `maxBlocks` (64), `maxRadius` (16.0), `activationMode` ('HOLD'), and whitelisted block tags.
- Edit with any text editor or via in-game configuration screens.

---

## 📄 License

This project is licensed under the terms of the **MIT License**.
See the [LICENSE](LICENSE) file for complete details.
