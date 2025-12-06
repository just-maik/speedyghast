# How to Install and Use SpeedyGhast

A Fabric mod that increases the flight speed of a Happy Ghast when the rider wears Soul Speed boots.

## Requirements

-   Minecraft 1.21.6
-   [Fabric Loader](https://fabricmc.net/)
-   [Fabric API](https://modrinth.com/mod/fabric-api)
-   [Cloth Config API](https://modrinth.com/mod/cloth-config)

## Installation

1.  Install **Fabric Loader** for Minecraft 1.21.6.
2.  Download **Fabric API** and **Cloth Config API** and place them in your `mods` folder.
3.  Download the latest `speedyghast-x.x.x.jar` from the releases page.
4.  Place the SpeedyGhast jar into your server's `mods` folder.
5.  Restart the server.

> **Note:** This is a server-side mod. Clients do not need to install it.

## Configuration

The config file is located at `config/speedyghast.json` and will be created on first run.

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

| Option             | Description                                                      |
| ------------------ | ---------------------------------------------------------------- |
| `base_speed`       | The default flying speed of the Happy Ghast                      |
| `speed_multiplier` | Multiplier applied based on Soul Speed enchantment level (I-III) |
| `check_interval`   | How often (in ticks) to check for speed updates (20 = 1 second)  |

If you have [Mod Menu](https://modrinth.com/mod/modmenu) installed, you can also configure these options in-game.

## Usage

1.  Tame and ride a **Happy Ghast**.
2.  Equip boots enchanted with **Soul Speed**.
3.  Enjoy the speed boost!

The higher the Soul Speed level, the faster you'll fly.
