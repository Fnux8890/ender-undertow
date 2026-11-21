# Cinematic beta for Java 26.3

Install the Fabric **or** Minecraft Forge JAR into the instance's `mods/` directory. Use the matching loader on the server and on each client that should see the cinematic. For singleplayer, the same JAR runs both sides. Fabric also needs Fabric API for bundled data pack discovery. The mod includes the encounter data. When upgrading a world from the standalone ZIP, remove the previous Ender Undertow ZIP from that world's `datapacks/` directory to avoid overriding the new functions.

The server chooses the carrier and destination, checks clearance again at release, and controls the teleport. A client that advertises cinematic support gets a longer lift sequence. The close-up, opening mouth and center-first focus pull use that client's real Enderman and destination. Turning the camera does not rotate the gameplay entity. A player without the client presentation follows the original eight-tick encounter whenever their server admits vanilla clients.

Minecraft's **Distortion Effects** setting below 50% selects a gentle neutral dim and skips camera rotation. The camera turn applies only in first person. Opening the pause menu skips presentation. Reconnection clears cinematic capability until the current client announces itself again.

During the scream the miner lowers both hands while the carrier reaches forward. The first-person view jerks upward, shudders twice and settles just before the teleport. Distortion Effects scales the shake and disables it below 50%. As the scream ends, the world slips out of focus and darkens to violet. At the teleport the tint shifts through blue, then the natural cave colors return along with focus. At the landing, detail returns near the crosshair first, then across the edges over about a second and a half, with a slight sway. The hotbar stays readable. Setting Distortion Effects below 50% also turns off the recovery blur and sway.

## The Gentleman

On Fabric and Forge, about one in 64 newly assessed Overworld Endermen wears a bowler hat and a curled moustache. When he catches a miner, he says “I'm terribly sorry, old chap” in an older British voice. The miner hears the line on the Voice/Speech sound channel. Minecraft subtitles also display it. The 1-in-32 home roll has first priority. On other rolls, the Gentleman looks for exposed diamond or deepslate diamond ore 12–48 blocks away in loaded terrain. If he finds a dry, walkable spot 3–6 blocks from the ore with a clear view, he lands you facing it. Otherwise he uses the selected cave.

To dress an existing Enderman for a rehearsal, choose one with an empty head slot:

```mcfunction
/execute as @e[type=minecraft:enderman,tag=!eu_rift,sort=nearest,limit=1] run function undertow:gentleman/equip
```

[Voice credits](VOICE-CREDITS.md)

## In-game recordings

[Watch the encounter with game audio (MP4)](https://github.com/Fnux8890/ender-undertow/releases/download/v0.2.0-beta.7/ender-undertow-sound-preview.mp4)

These silent GIFs were captured from the shipped 26.3 JARs in a local survival world. The test cave uses night vision to make the face and the destination visible. A gravel scrape comes from the carrier as it grabs you. The vanilla Enderman stare swells as the jaw opens. The scream peaks with the camera recoil and cuts out briefly before a dull stone impact at the landing. Hostile Creatures volume controls the scrape and scream. A three-second portal resonance fades under the returning cave ambience after every cinematic landing. Players volume controls both landing cues.

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

The cinematic starts at hold 62. The first eight steps use the existing ceiling-checked lift. At hold 18 the victim hears the Enderman scream. At hold 6 the world starts to lose focus. At hold zero the server revalidates the landing. Success starts the center-first recovery at the destination. Cancellation releases the camera and clears the overlay. The client also times out if the server stops sending events.
