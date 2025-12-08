# SpeedyGhast
[![Build Mod](https://github.com/just-maik/speedyghast/actions/workflows/build.yml/badge.svg)](https://github.com/just-maik/speedyghast/actions/workflows/build.yml)
[![Release](https://github.com/just-maik/speedyghast/actions/workflows/release.yml/badge.svg)](https://github.com/just-maik/speedyghast/actions/workflows/release.yml)

A Fabric mod for Minecraft 1.21.6 that increases the flight speed of a Happy Ghast when the rider wears Soul Speed boots.

## Features
-   **Server-Side Only**: No client installation required.
-   **Configurable**: Adjust base speed and multipliers for each Soul Speed level.
-   **Optional Cloth Config**: Use Cloth Config for in-game configuration, or edit JSON file directly.

## Building
### Option 1: With Gradle (if installed)
```bash
gradle build
```

### Option 2: With Docker (no Java/Gradle needed)
Run the included script:
```cmd
build-docker.bat
```
Or manually:
```bash
docker build -t speedyghast .
docker run --rm -v "%cd%/build:/home/gradle/project/build" speedyghast gradle build
```

## Configuration
The config file is located at `config/speedyghast.json` and will be created automatically on first run.

### Option 1: Edit JSON File (Always Available)
```json
{
  "base_speed": 0.05,
  "speed_multiplier": {
    "level_1": 1.5,
    "level_2": 2.0,
    "level_3": 2.5
  },
  "check_interval": 20
}
```

-   `base_speed`: The default flying speed of the Ghast.
-   `speed_multiplier`: Multiplier applied based on Soul Speed enchantment level.
-   `check_interval`: How often (in ticks) to check for speed updates (20 ticks = 1 second).

### Option 2: In-Game Config Screen (Requires Cloth Config + Mod Menu)
If you install [Cloth Config API](https://modrinth.com/mod/cloth-config) and [Mod Menu](https://modrinth.com/mod/modmenu), you can configure these settings in-game through the Mods menu.

## Usage
1.  Ride a Happy Ghast.
2.  Equip boots with **Soul Speed**.
3.  Enjoy the boost!
