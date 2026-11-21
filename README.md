# Ender Undertow

![Two violet eyes above a rift in a cave floor](art/ender_undertow.png)

A survival data pack for **Minecraft Java Edition 26.3** and **Horizon 26.3**. Mine underground with an Enderman close behind you and it can lift you off the floor, then blink you into a nearby cave. Facing it prevents the grab. A wall between you blocks its reach.

## Cinematic beta for Minecraft Java 26.3

The optional mod turns the caught miner toward the Enderman as it opens its mouth and screams. The cave slips out of focus and darkens to violet. The tint passes through blue as sight returns. At the landing, detail returns around the crosshair first and gradually reaches the corners. The server chooses and validates the destination. Other miners keep their own view.

On Fabric and Forge, about one in 64 Endermen is **the Gentleman**. His charcoal bowler and curled moustache give him away. If he grabs you, he offers a spoken “I'm terrible sorry, old chap” before the teleport. [Watch the encounter with sound](https://github.com/Fnux8890/ender-undertow/releases/download/v0.2.0-beta.10/gentleman-encounter.mp4).

Choose the **Fabric** or **Minecraft Forge** JAR from the [cinematic beta release](https://github.com/Fnux8890/ender-undertow/releases/tag/v0.2.0-beta.10). Each JAR includes the data pack. See the [installation, accessibility, and targeted rehearsal guide](cinematic/README.md).

## Play

[Download the data pack ZIP for Java Edition or Horizon](https://github.com/Fnux8890/ender-undertow/releases/download/v0.2.0-beta.9/ender-undertow-0.2.0-beta.9.zip). Keep the file zipped. The same download works on both.

- **New Java world:** Open **Data Packs** during world creation, add the ZIP, and enable Ender Undertow.
- **Existing Java or Horizon world:** Put the ZIP in that world's `datapacks/` folder and reopen the world.
- **Java dedicated server:** Put the ZIP in the active world folder's `datapacks/` directory and restart the server or run `/reload` as an operator.

Run `/datapack list enabled` to check the installation. When upgrading, replace the previous ZIP. Forge and Fabric are optional. In a modded Java 26.3 instance, use the same world data pack installation path. Vanilla Java 26.3 and Horizon 26.3 are the tested runtimes. Older Minecraft versions and specific Forge/Fabric modpacks have not been verified.

The pack runs on its own in Overworld survival. Breaking a tracked stone or ore block with an appropriate tool gives the Enderman a 1-in-8 chance if it is within reach behind you. Each pickup starts a two-minute cooldown for that player. Your inventory travels with you.

About one in sixteen Overworld Endermen is a **Rift Enderman**. Look for blue soul-fire sparks around its shoulders before you turn your back. During a grab it first searches loaded terrain 64 to 128 blocks away, within four blocks of your mining level. If the search finds no safe far landing, it tries the ordinary nearby caves. The separate 1-in-32 home roll still takes priority at release. [See the Rift Enderman in-game](cinematic/media/rift-enderman.gif).

Ordinary landings are sampled within 16 blocks on each horizontal axis and four blocks up or down. A diagonal can be about 23 blocks. Each completed carry rolls once for a 1-in-32 (3.125%) chance to take you to a safe spot at or beside your personal Overworld bed or admin-set spawn point. The home trip has no cave-distance cap. A missing bed, a spawn in another dimension, or an unsafe home falls back to the selected cave. The Fabric and Forge mods can load a distant home on demand. The standalone data pack requires the home area to be loaded.

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
python tools/test_ender_undertow.py --client PATH_TO_CLIENT --resources PATH_TO_RESOURCES --pack-zip dist/ender-undertow-0.2.0-beta.9.zip --seed 23
python tools/test_mining_input.py --client PATH_TO_CLIENT --control PATH_TO_HORIZON_CTL --resources PATH_TO_RESOURCES --pack-zip dist/ender-undertow-0.2.0-beta.9.zip
```

The 0.1.1 ZIP passed 24 integrated-server checks on each of two seeds. An offscreen survival client also mined stone with a diamond pickaxe and teleported with full health and air. Multiplayer guest behavior still needs a playtest.

## Contributing

Issues and pull requests are welcome. For an encounter bug, include your Horizon build, pack version, solo or multiplayer mode, and steps to reproduce it. For a landing bug, include the blocks at the destination and immediately around it. The source and original icon are available under the [MIT license](LICENSE).
