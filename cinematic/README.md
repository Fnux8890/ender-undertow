# Cinematic beta for Java 26.3

Install the Fabric **or** Minecraft Forge JAR into the instance's `mods/` directory. Use the matching loader on the server and on each client that should see the cinematic. For singleplayer, the same JAR runs both sides. Fabric also needs Fabric API for bundled data pack discovery. The mod includes the encounter data. When upgrading a world from the standalone ZIP, remove the previous Ender Undertow ZIP from that world's `datapacks/` directory to avoid overriding the new functions.

The server chooses the carrier and destination, checks clearance again at release, and controls the teleport. A client that advertises cinematic support gets a longer lift sequence. The close-up, opening mouth, violet eyelids and reveal use that client's real Enderman and destination. Turning the camera does not rotate the gameplay entity. A player without the client presentation follows the original eight-tick encounter whenever their server admits vanilla clients.

Minecraft's **Distortion Effects** setting below 50% selects a simple fade and skips camera rotation. The camera turn applies only in first person. Opening the pause menu skips presentation. Reconnection clears cinematic capability until the current client announces itself again.

During the scream the miner lowers both hands while the carrier reaches forward. After the blink the cave comes into focus over about a second and a half, with a slight sway as the player regains sight. Setting Distortion Effects below 50% also turns off the recovery blur and sway.

## In-game recordings

These silent GIFs were captured from the shipped 26.3 JARs in a local survival world. The test cave uses night vision to make the face and the destination visible. The game plays the Enderman scream during the encounter.

[Fabric recording](media/fabric.gif) · [Forge recording](media/forge.gif)

## Target a rehearsal

Use a disposable Overworld survival world with cheats. Stand in a dry, well-lit underground room with solid floor and three blocks of headroom. Provide open floor seven to sixteen blocks away within four blocks of your elevation. Keep looking straight ahead. These commands summon a stationary test carrier behind the chosen player, then exercise the real selection, lift, destination search, obstruction checks, and teleport. Replace `Miner` with the player's name.

```mcfunction
/execute as Miner at @s rotated ~ 0 positioned ^ ^ ^-1.5 run summon minecraft:enderman ~ ~ ~ {NoAI:1b,PersistenceRequired:1b,Tags:["undertow_rehearsal"]}
/execute as Miner at @s run function undertow:test/encounter
```

The function bypasses the mining roll and cooldown for the selected rehearsal. If the original carrier disappears or no safe destination exists, the player stays in the cave. To clean up the test carrier, run `/kill @e[type=minecraft:enderman,tag=undertow_rehearsal]`. To test natural gameplay, mine stone without the test function. The default chance is one in eight eligible mining ticks.

## Build and iterate

Use Java 25, an official launcher installation with Minecraft 26.3, Fabric Loader 0.20.1, and Forge 66.1.8. Run from the repository root:

```sh
python cinematic/build.py --game /path/to/launcher-directory --javac /path/to/jdk-25/bin/javac
```

The build uses Mojang's unobfuscated client and the locally installed libraries for compilation. Both distributable JARs go to `dist/`. For data pack edits use a loose development pack and `/reload`. For method-body changes, start a development instance under a Java debugger and reload the compiled classes. Restart after changing mixin targets, metadata, dependencies, or the set of fields and methods. [Fabric debugging guide](https://docs.fabricmc.net/develop/debugging)

## Timing

The cinematic starts at hold 27. The first eight steps use the existing ceiling-checked lift. At hold 12 the victim hears the Enderman scream. At hold 6 the purple lids start to close. At hold zero the server revalidates the landing. Success starts an eye-shaped opening onto the actual world. Cancellation releases the camera and clears the overlay. The client also times out if the server stops sending events.
