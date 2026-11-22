# Ender Undertow for Horizon 26.3

One more swing of your pickaxe. A scrape behind you. Turn around in time and the Enderman lets you be. Keep mining and it can lift you off the cave floor. As your sight returns, you might recognize the tunnel. If your captor wore a bowler hat, take a closer look at the stone nearby.

[Try the encounter preview on Michado](https://michado.dk/projects/ender-undertow/).

This development build uses Horizon’s proposed composable post effects. It requires a client and host built against the revision in `native/horizon-mod.toml`. For the published November 20 Nightly, use the [beta.2 bundle and installation guide](https://github.com/Fnux8890/ender-undertow/releases/tag/horizon-26.3-beta.2).

[Watch the in-game encounter with Golden Hour enabled](../media/listings/focus-with-golden-hour.gif). The blue clears from the center as the player lands in the new tunnel.

## Install

1. Extract the outer beta bundle. Keep the two ZIPs inside it zipped.
2. Install `ender_undertow_cinematic.hmod` with the Horizon launcher's native mod installer on the pinned instance. In multiplayer, install it on the host and each player who wants the cinematic.
3. Put the **data** ZIP in the world's `datapacks/` directory. Replace any previous Ender Undertow data pack in that world. Reopen it or run `/reload`.
4. Put the **resources** ZIP in the instance's `resourcepacks/` directory. Enable it in Resource Packs for the bowler, moustache, recorded apology and portal resonance.
5. Keep your preferred shader pack selected. Cinematic supplies the brief Focus blur and warble. Set Distortion Effects to zero to disable the distortion.
6. Run `/trigger eu.scene set 1` in game to opt in. Use `/trigger eu.scene set 2` for ordinary grabs. Enable it again after reconnecting. Horizon's Distortion Effects option scales the shake.

## Encounters

Mine underground with an Enderman close behind you for a chance of being lifted and teleported. Facing it prevents the grab. The cinematic turns the caught miner toward the carrier, lowers their hands and restores sight from the center after landing. A Rift Enderman has soul-fire sparks and first searches 64 to 128 blocks away.

About one Enderman in 64 is the Gentleman. During his apology, the pack surveys loaded terrain 12 to 48 blocks from the pickup for regular or deepslate diamond ore. It checks for a dry overlook three blocks from an exposed face. The vein, sightline, floor, headroom and escape space are checked again at release. A mined vein or unsafe landing falls back to the regular cave route. The one-in-32 home roll takes priority. A distant home can be unloaded. Horizon briefly loads the missing corners of its footprint and releases the tickets it added after checking the landing.

## Rehearsal

In a disposable survival world, opt in and stand with a dry cave nearby. Summon an Enderman directly behind you. Run `/execute as @e[type=enderman,sort=nearest,limit=1] at @s run function #undertow:gentleman` to equip him, then `/execute as @s at @s run function undertow:test/encounter`. The rehearsal uses the production carrier and landing checks. Remove the original carrier during a second lift to test cancellation. In multiplayer, a second miner can watch without opting in.

## Build from source

Use the Horizon tools for the pinned build. From the repo root, run

```sh
python horizon/build.py
(cd horizon/native && cargo horizon-mod build)
python horizon/build.py --native horizon/native/target/horizon-mod/ender_undertow_cinematic.hmod
```

The bundle is written to `dist/`. The data and resource packs reuse the shared encounter functions and cinematic assets. Run `cargo test` in the native crate after fetching the SDK. The game harness exercises the actual data ZIP in a disposable integrated-server world.

```sh
python tools/test_ender_undertow.py --client /path/to/horizon-client --resources /path/to/prepared/resources --pack-zip dist/ender-undertow-horizon-data-26.3-beta.3-dev.zip --cinematic --horizon-parity --seed 41
```
