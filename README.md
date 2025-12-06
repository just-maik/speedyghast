# SpeedyGhast
[![Build Mod](https://github.com/just-maik/speedyghast/actions/workflows/build.yml/badge.svg)](https://github.com/just-maik/speedyghast/actions/workflows/build.yml)

A Fabric mod for Minecraft 1.21.6 that increases the flight speed of a Happy Ghast when the rider wears Soul Speed boots.

## Features
-   **Server-Side Only**: No client installation required.
-   **Configurable**: Adjust base speed and multipliers for each Soul Speed level.

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
The config file is located at `config/speedyghast.json`.

```json
{
  "base_speed": 0.05,
  "speed_multiplier": {
    "level_1": 1.5,
    "level_2": 2.0,
    "level_3": 3.0
  },
  "check_interval": 20
}
```

-   `base_speed`: The default flying speed of the Ghast.
-   `speed_multiplier`: Multiplier applied based on Soul Speed enchantment level.
-   `check_interval`: How often (in ticks) to check for speed updates (20 ticks = 1 second).

## Usage
1.  Ride a Happy Ghast.
2.  Equip boots with **Soul Speed**.
3.  Enjoy the boost!
