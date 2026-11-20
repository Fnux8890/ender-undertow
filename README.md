# Ender Undertow

![Two violet eyes above a rift in a cave floor](art/ender_undertow.png)

A survival encounter for Horizon 26.3. Mine underground with an Enderman close behind you and it can lift you off the floor, then blink you into a nearby cave. Facing it prevents the grab. A wall between you blocks its reach.

## Play

1. Download [Ender Undertow 0.1.1](https://github.com/Fnux8890/ender-undertow/releases/download/v0.1.1/ender-undertow-0.1.1.zip).
2. Put the ZIP in your world's `datapacks/` folder. If upgrading, replace the older ZIP.
3. Reopen the world or run `/reload`. Use `/datapack list enabled` to confirm the pack is active.

The pack runs on its own in Overworld survival. Breaking a tracked stone or ore block with an appropriate tool gives the Enderman a 1-in-8 chance if it is within reach behind you. Each pickup starts a two-minute cooldown for that player. Your inventory travels with you.

The landing needs solid ground, two dry air blocks and an adjacent space you can walk into. The pack checks again just before teleporting. If the Enderman disappears or the spot becomes unsafe, the carry ends.

## Tune it for a modpack

Run `/scoreboard players set #odds eu.roll 32` for a 1-in-32 chance. Use `1` to try an encounter on every eligible break, or `8` to restore the default. The [pack guide](datapacks/ender_undertow/README.md) covers reach, mining statistics, landing rules and cooldowns.

## Build and test

The editable pack is in [datapacks/ender_undertow](datapacks/ender_undertow/). To build an installable ZIP, run this command from the repository root with Python 3.

```sh
python tools/build.py
```

For gameplay changes, use a Horizon client with its command harness and your own prepared 26.3 resources.

```sh
python tools/test_ender_undertow.py --client PATH_TO_CLIENT --resources PATH_TO_RESOURCES --pack-zip dist/ender-undertow-0.1.1.zip --seed 23
python tools/test_mining_input.py --client PATH_TO_CLIENT --control PATH_TO_HORIZON_CTL --resources PATH_TO_RESOURCES --pack-zip dist/ender-undertow-0.1.1.zip
```

The 0.1.1 ZIP passed 24 integrated-server checks on each of two seeds. An offscreen survival client also mined stone with a diamond pickaxe and teleported with full health and air. Multiplayer guest behavior still needs a playtest.

## Contributing

Issues and pull requests are welcome. For an encounter bug, include your Horizon build, pack version, solo or multiplayer mode, and steps to reproduce it. For a landing bug, include the blocks at the destination and immediately around it. The source and original icon are available under the [MIT license](LICENSE).
