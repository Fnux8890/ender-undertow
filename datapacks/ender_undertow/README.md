# Ender Undertow

A Horizon 26.3 world data pack. A close Enderman behind a survival miner can lift them briefly and blink them into another cave. Chapterloom is optional.

## Install

Copy the ZIP into your world's `datapacks/` directory, then reopen the world or run `/reload`. Run `/datapack list enabled` to check that Ender Undertow is active. To install from source, copy this folder with its `pack.mcmeta` instead of the ZIP.

## Encounter

Breaking one of the tracked stone or ore blocks opens one chance for that player that tick. Holding a pickaxe, swinging, or walking through a cave does not count. The player must be in Overworld survival, at least five blocks below the ocean-floor heightmap, with an Enderman in a small area immediately behind their horizontal facing. Looking down while mining still respects which way the player is facing. The heightmap can include constructed roofs.

The default chance is 1 in 8 per qualifying mining tick. A successful pickup starts a two-minute cooldown for that player. The eight-tick lift uses short upward movements and checks headroom. A clear reach path is required. Losing the original Enderman interrupts the carry, even if a different Enderman is standing nearby. The miner keeps their inventory.

The pack samples at most 32 random columns in a square extending 16 blocks in each horizontal direction. It skips the closest seven blocks and checks up to four blocks above and below the starting height. A landing needs a loaded chunk, a whitelisted full floor, two dry air blocks, and an adjacent walkable air column. It rejects water or lava directly overhead and exposed lava touching the sides of the landing. It checks the landing again before the final teleport. A failed search leaves the miner where they are.

About one in sixteen Overworld Endermen is a **Rift Enderman**. Look for blue soul-fire sparks around its shoulders before you turn your back. During a grab it first searches loaded terrain 64 to 128 blocks away, within four blocks of your mining level. If the search finds no safe far landing, it tries the ordinary nearby caves. The separate 1-in-32 home roll still takes priority at release.

Ordinary landings are sampled within 16 blocks on each horizontal axis and four blocks up or down. A diagonal can be about 23 blocks. Each completed carry rolls once for a 1-in-32 (3.125%) chance to take you to a safe spot at or beside your personal Overworld bed or admin-set spawn point. The home trip has no cave-distance cap. A missing bed, a spawn in another dimension, or an unsafe home falls back to the selected cave. The Fabric and Forge mods can load a distant home on demand. The standalone data pack requires the home area to be loaded.

To summon the variant for a modpack encounter, use:

```mcfunction
/summon minecraft:enderman ~ ~ ~ {Tags:["eu_rift","eu_assessed"]}
```

## Tuning

For a more occasional encounter, run `/scoreboard players set #odds eu.roll 32`. Use `1` to force the chance roll while testing and `8` to restore the default. Use positive whole numbers. Reloading preserves this setting. A break must award its Minecraft mined statistic, so use a suitable tool. The mined block types are listed in `data/undertow/function/load.mcfunction`. Landing materials are in `data/undertow/tags/block/floor.json`.

## Verification

From the workspace root, run the following with a Horizon binary that supports the command harness and locally prepared 26.3 resources.

```sh
python tools/test_ender_undertow.py --client PATH_TO_HORIZON_CLIENT --resources PATH_TO_26_3_RESOURCES
```

The test uses a fresh temporary world and drives the integrated server. It sets the mined statistic objective to simulate a block break, then lets the ordinary tick function run. It checks the surface and facing exclusions, idle miners, pickup, random relocation, cooldown state, unsafe landings and exhausted searches. Use `--pack-zip PATH` to exercise the distributable and `--seed N` to vary the world. The 0.1.1 audit also exercised the actual survival attack-button mining path with a diamond pickaxe in an offscreen Horizon client. Multiplayer guests have not been playtested.
