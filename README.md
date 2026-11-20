# Ender Undertow

![Two violet eyes above a rift in a cave floor](art/ender_undertow.png)

A survival data pack for **Minecraft Java Edition 26.3** and **Horizon 26.3**. Mine underground with an Enderman close behind you and it can lift you off the floor, then blink you into a nearby cave. Facing it prevents the grab. A wall between you blocks its reach.

## Play

[Download the data pack ZIP for Java Edition or Horizon](https://github.com/Fnux8890/ender-undertow/releases/download/v0.1.1/ender-undertow-0.1.1.zip). Keep the file zipped. The same download works on both.

- **New Java world:** Open **Data Packs** during world creation, add the ZIP, and enable Ender Undertow.
- **Existing Java or Horizon world:** Put the ZIP in that world's `datapacks/` folder and reopen the world.
- **Java dedicated server:** Put the ZIP in the active world folder's `datapacks/` directory and restart the server or run `/reload` as an operator.

Run `/datapack list enabled` to check the installation. When upgrading, replace the previous ZIP. Forge and Fabric are optional. In a modded Java 26.3 instance, use the same world data pack installation path. Vanilla Java 26.3 and Horizon 26.3 are the tested runtimes. Older Minecraft versions and specific Forge/Fabric modpacks have not been verified.

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
