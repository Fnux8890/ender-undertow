"""Exercise the data pack on Horizon's actual headless integrated server."""
import argparse
import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--client', type=Path, required=True)
parser.add_argument('--resources', type=Path, required=True)
parser.add_argument('--cinematic', action='store_true', help='Also verify targeted cinematic timing and cancellation.')
parser.add_argument('--horizon-parity', action='store_true', help='Verify the Horizon cinematic companion data pack.')
parser.add_argument('--seed', type=int, default=23)
parser.add_argument('--pack-zip', type=Path, help='Install the actual release ZIP in the test world.')
args = parser.parse_args()
ops = []
expected = {}

def cmd(command):
    ops.append({'op': 'cmd', 'cmd': command})

def check(command, value=1):
    cmd(command)
    expected[len([op for op in ops if op['op'] == 'cmd']) - 1] = value

def tick(n):
    ops.append({'op': 'tick', 'n': n})

if not args.horizon_parity:
    cmd('reload')
tick(2)
cmd('tp @s 8.5 8 8.5 0 0')
ops.append({'op': 'settle'})
cmd('fill -10 6 -10 26 14 26 minecraft:stone')
cmd('fill -9 7 -9 25 12 25 minecraft:air')
cmd('gamemode survival')
cmd('scoreboard players set #odds eu.roll 1')
if args.horizon_parity:
    # Prepare a remote personal spawn, allow it to unload while the rest of
    # the suite runs, and retain one neighbouring ticket owned by the fixture.
    cmd('forceload add 8190 8190 8193 8193')
    cmd('fill 8189 219 8189 8195 219 8195 stone')
    cmd('fill 8189 220 8189 8195 223 8195 air')
    cmd('forceload remove 8190 8190 8193 8193')
    cmd('forceload add 8193 8193')

# Mine on the surface with an Enderman directly behind the player.
cmd('fill 6 300 5 10 300 10 minecraft:stone')
cmd('tp @s 8.5 301 8.5 0 0')
cmd('summon minecraft:enderman 8.5 301 7.2 {NoAI:1b,PersistenceRequired:1b}')
cmd('scoreboard players set @s eu.mine0 1')
tick(1)
check('execute unless score @s eu.hold matches 1..')
cmd('tp @s 8.5 7 8.5 0 0')
cmd('summon minecraft:enderman 8.5 7 7.2 {NoAI:1b,PersistenceRequired:1b}')
cmd('scoreboard players set #odds eu.roll 1')
tick(1)
check('execute unless score @s eu.hold matches 1..')
# A wall near the Enderman, beyond the first reach sample.
cmd('tp @s 8.5 7 8.7 0 0')
cmd('tp @e[type=minecraft:enderman,distance=..4] 8.5 7 6.65')
cmd('setblock 8 8 7 minecraft:stone')
cmd('scoreboard players set @s eu.mine0 1')
tick(1)
check('execute unless score @s eu.hold matches 1..')
cmd('setblock 8 8 7 minecraft:air')
cmd('tp @e[type=minecraft:enderman,distance=..4] 8.5 7 7.2')
cmd('gamemode creative')
cmd('scoreboard players set @s eu.mine0 1')
tick(1)
check('execute unless score @s eu.hold matches 1..')
cmd('gamemode survival')
cmd('tp @s 8.5 7 8.5 180 0')
cmd('scoreboard players set @s eu.mine0 1')
tick(1)
check('execute unless score @s eu.hold matches 1..')
cmd('tp @s 8.5 7 8.5 0 0')
cmd('scoreboard players set @s eu.mine0 1')
tick(1)
check('scoreboard players get @s eu.hold', 8)
check('execute positioned 8.5 7.18 8.5 if entity @s[distance=..0.1]')
tick(8)
check('execute positioned 8.5 7 8.5 unless entity @s[distance=..6]')
check('execute at @s if block ~ ~-1 ~ #undertow:floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air')
check('execute if score @s eu.cool matches 2390..2400')
cmd('tp @s 8.5 7 8.5 0 0')
cmd('scoreboard players set @s eu.mine0 1')
tick(1)
check('execute unless score @s eu.hold matches 1..')
# Mutate the destination between selection and arrival. A late obstruction must cancel.
for block, height in [('lava', 0), ('water', 1), ('stone', 0), ('air', -1)]:
    cmd('tp @s 8.5 7 8.5')
    cmd('setblock 0 6 0 minecraft:stone')
    cmd('setblock 0 7 0 minecraft:air')
    cmd('setblock 0 8 0 minecraft:air')
    cmd(f'setblock 0 {7+height} 0 minecraft:{block}')
    for axis, coord in zip('xyz', [0,7,0]):
        cmd(f'scoreboard players set @s eu.{axis} {coord}')
    cmd('function undertow:release')
    check('execute positioned 8.5 7 8.5 if entity @s[distance=..0.1]')

# A valid destination is our positive control for every late-hazard case.
cmd('fill -2 6 -2 2 6 2 minecraft:stone')
cmd('fill -2 7 -2 2 9 2 minecraft:air')
cmd('function undertow:arrive {x:0,y:7,z:0}')
check('execute positioned 0.5 7 0.5 if entity @s[distance=..0.01]')
for hazard, position in [('lava', '0 9 0'), ('lava', '1 7 0'), ('water', '0 9 0')]:
    cmd('tp @s 8.5 7 8.5')
    cmd(f'setblock {position} minecraft:{hazard}')
    cmd('function undertow:arrive {x:0,y:7,z:0}')
    check('execute positioned 8.5 7 8.5 if entity @s[distance=..0.01]')
    cmd(f'setblock {position} minecraft:air')

# Replacing the original Enderman mid-lift must cancel even with a second one nearby.
cmd('tp @s 8.5 7 8.5 0 0')
cmd('scoreboard players reset @s eu.cool')
cmd('scoreboard players set @s eu.mine0 1')
tick(1)
check('scoreboard players get @s eu.hold', 8)
cmd('tp @e[type=minecraft:enderman,distance=..4] 16 7 16')
cmd('summon minecraft:enderman 8.5 7 7.2 {NoAI:1b,PersistenceRequired:1b}')
tick(8)
check('execute positioned 8.5 7 8.5 if entity @s[distance=..3]')
cmd('tp @s 8.5 7 8.5 0 0')
# A sealed two-block air pocket is ineligible, even with solid ground.
cmd('fill -1 6 -1 1 8 1 minecraft:stone')
cmd('fill 0 7 0 0 8 0 minecraft:air')
cmd('function undertow:arrive {x:0,y:7,z:0}')
check('execute positioned 8.5 7 8.5 if entity @s[distance=..0.1]')
# No other dry landing in range: the search must finish without starting a carry.
cmd('fill -9 2 -9 25 14 25 minecraft:stone')
cmd('fill 7 7 6 9 12 9 minecraft:air')
cmd('scoreboard players reset @s eu.cool')
cmd('tp @s 8.5 7 8.5 0 0')
cmd('scoreboard players set @s eu.mine0 1')
tick(1)
check('scoreboard players get @s eu.tries', 0)
check('execute unless score @s eu.hold matches 1..')

if args.cinematic:
    cmd('tp @s 8.5 7 8.5 0 0')
    cmd('fill -9 6 -9 25 6 25 minecraft:stone')
    cmd('fill -9 7 -9 25 12 25 minecraft:air')
    cmd('kill @e[type=minecraft:enderman]')
    cmd('summon minecraft:enderman 8.5 7 7.2 {NoAI:1b,PersistenceRequired:1b}')
    cmd('tag @s add eu.cinematic')
    cmd('scoreboard players reset @s eu.hold')
    cmd('execute as @s at @s run function undertow:test/encounter')
    check('scoreboard players get @s eu.hold',62)
    check('execute if entity @s[tag=eu.in_scene]')
    tick(56)
    check('scoreboard players get @s eu.hold',6)
    tick(6)
    check('execute unless entity @s[tag=eu.in_scene]')
    check('execute positioned 8.5 7 8.5 unless entity @s[distance=..6]')
    cmd('tp @s 8.5 7 8.5 0 0')
    cmd('execute as @s at @s run function undertow:test/encounter')
    check('scoreboard players get @s eu.hold',62)
    cmd('kill @e[type=minecraft:enderman,distance=..4]')
    tick(3)
    check('execute unless entity @s[tag=eu.in_scene]')
    check('execute positioned 8.5 7 8.5 if entity @s[distance=..2]')

# Exercise both random outcomes deterministically with a saved personal spawn.
cmd('tp @s 8.5 7 8.5')
cmd('fill -2 6 -2 2 6 2 minecraft:stone')
cmd('fill -2 7 -2 2 10 2 minecraft:air')
cmd('spawnpoint @s 20 7 20')
cmd('fill 18 6 18 22 6 22 minecraft:oak_planks')
cmd('fill 18 7 18 22 10 22 minecraft:air')
for axis, value in [('x', 0), ('y', 7), ('z', 0)]:
    cmd(f'scoreboard players set @s eu.{axis} {value}')
cmd('scoreboard players set @s eu.roll 2')
cmd('execute at @s run function undertow:route')
check('execute positioned 0.5 7 0.5 if entity @s[distance=..0.1]')
cmd('scoreboard players set @s eu.roll 1')
cmd('execute at @s run function undertow:route')
check('execute positioned 20.5 7 20.5 if entity @s[distance=..0.1]')
cmd('tp @s 8.5 7 8.5')
cmd('fill 18 7 18 22 10 22 minecraft:lava')
cmd('execute at @s run function undertow:route')
check('execute positioned 0.5 7 0.5 if entity @s[distance=..0.1]')

# A remote landing at a known offset, independent of the random column sampler.
cmd('tp @s 80.5 200 8.5')
ops.append({'op': 'settle'})
cmd('forceload add 78 6 82 10')
cmd('fill 78 199 6 82 199 10 minecraft:stone')
cmd('fill 78 200 6 82 204 10 minecraft:air')
cmd('tp @s 8.5 200 8.5')
cmd('scoreboard players set @s eu.dx 72')
cmd('scoreboard players set @s eu.dz 0')
cmd('scoreboard players set @s eu.found 0')
cmd('execute at @s run function undertow:rift/column {x:72,z:0}')
check('scoreboard players get @s eu.found')
check('scoreboard players get @s eu.x',80)
cmd('scoreboard players set @s eu.roll 2')
cmd('execute at @s run function undertow:route')
check('execute positioned 80.5 200 8.5 if entity @s[distance=..0.1]')
cmd('tp @s 8.5 200 8.5')
cmd('setblock 80 200 8 minecraft:lava')
cmd('scoreboard players set @s eu.found 0')
cmd('execute at @s run function undertow:rift/column {x:72,z:0}')
check('scoreboard players get @s eu.found',0)
cmd('fill 12 199 6 16 199 10 minecraft:stone')
cmd('fill 12 200 6 16 202 10 minecraft:air')
cmd('execute at @s run function undertow:rift/column {x:6,z:0}')
check('scoreboard players get @s eu.found',0)
cmd('summon minecraft:enderman 8 200 7 {NoAI:1b,Tags:["eu_rift","eu_assessed","test_rift"]}')
cmd('execute at @s run function undertow:rift/pulse')
check('execute if entity @e[tag=test_rift,tag=eu_rift,tag=eu_assessed]')
cmd('forceload remove 78 6 82 10')
# The variant still offers a nearby escape when the far search has no floor.
cmd('tp @s 8.5 309 8.5 0 0')
ops.append({'op': 'settle'})
cmd('forceload add -8 -8 25 25')
cmd('fill -8 308 -8 25 308 25 minecraft:stone')
cmd('kill @e[type=minecraft:enderman]')
cmd('summon minecraft:enderman 8.5 309 7.2 {NoAI:1b,Tags:["eu_rift","eu_assessed"]}')
cmd('scoreboard players reset @s eu.hold')
cmd('execute at @s run function undertow:test/encounter')
check('execute if score @s eu.hold matches 1..')
check('execute if score @s eu.dx matches -16..16 if score @s eu.dz matches -16..16')
cmd('forceload remove -8 -8 25 25')

if args.horizon_parity:
    cmd('spawnpoint @s 8192 220 8192')
    check('execute unless loaded 8190 220 8190')
    cmd('scoreboard players set @s eu.roll 1')
    cmd('execute at @s run function undertow:route')
    check('execute positioned 8192.5 220 8192.5 if entity @s[distance=..0.1]')
    check('forceload query 8193 8193')
    cmd('execute store success score #test_ticket eu.open run forceload query 8190 8190')
    check('scoreboard players get #test_ticket eu.open', 0)
    cmd('forceload remove 8193 8193')
    cmd('tp @s 8.5 201 8.5')
    ops.append({'op':'settle'})
    cmd('kill @e[type=minecraft:enderman]')
    cmd('tp @s 8.5 201 8.5')
    ops.append({'op': 'settle'})
    cmd('fill 4 200 4 26 200 12 minecraft:stone')
    cmd('fill 4 201 4 26 204 12 minecraft:air')
    cmd('summon minecraft:enderman 8.5 201 7.2 {NoAI:1b,Tags:["escort_test"]}')
    cmd('execute as @e[tag=escort_test] at @s run function #undertow:gentleman')
    check('execute if entity @e[tag=escort_test,tag=eu_gentleman]')
    cmd('setblock 22 202 8 minecraft:deepslate_diamond_ore')
    cmd('execute at @s run function undertow:horizon/begin')
    # dx=14 is visited at hold 48, after anchoring the scan at the pickup.
    cmd('scoreboard players set @s eu.hold 48')
    cmd('execute at @s run function undertow:horizon/survey')
    check('scoreboard players get @s eu.vein')
    cmd('scoreboard players reset @s eu.hold')
    cmd('execute at @s run function undertow:gentleman/escort')
    check('execute positioned 22.5 202 8.5 if entity @s[distance=2.9..3.3]')
    cmd('tp @s 8.5 201 8.5')
    cmd('fill 19 201 5 25 203 11 minecraft:lava')
    cmd('setblock 22 202 8 minecraft:diamond_ore')
    cmd('execute at @s run function undertow:gentleman/escort')
    check('execute positioned 8.5 201 8.5 if entity @s[distance=..0.01]')
    # Put a vein well above mining level, beyond the standalone cave sampler.
    cmd('setblock 22 202 8 minecraft:air')
    cmd('fill 19 216 5 25 216 11 minecraft:stone')
    cmd('fill 19 217 5 25 221 11 minecraft:air')
    cmd('setblock 22 218 8 minecraft:diamond_ore')
    cmd('fill 18 201 4 26 204 12 minecraft:air')
    cmd('fill 4 205 4 12 205 12 minecraft:stone')
    cmd('weather clear')
    cmd('fill -8 200 -8 25 200 25 minecraft:stone')
    cmd('tp @s 8.5 201 8.5 0 0')
    cmd('tag @s add eu.cinematic')
    cmd('scoreboard players reset @s eu.hold')
    cmd('execute at @s run function undertow:test/encounter')
    check('execute if entity @s[tag=eu.in_scene,tag=eu.gent_scene]')
    check('scoreboard players get @s eu.hold',62)
    tick(16)
    check('scoreboard players get @s eu.hold',46)
    check('scoreboard players get @s eu.vein')
    check('scoreboard players get @s eu.orey',218)
    # Force the non-home branch once the normal survey has run.
    cmd('scoreboard players set @s eu.roll 2')
    cmd('execute at @s run function undertow:route')
    check('execute positioned 22.5 218 8.5 if entity @s[distance=2.9..3.3]')
    check('execute unless entity @s[tag=eu.in_scene]')
    cmd('scoreboard players reset @s eu.hold')
    cmd('tp @s 8.5 201 8.5')
    cmd('setblock 22 218 8 minecraft:air')
    cmd('execute at @s run function undertow:gentleman/escort')
    check('execute positioned 8.5 201 8.5 if entity @s[distance=..0.01]')
    cmd('tag @s remove eu.cinematic')
    tick(2)
    cmd('trigger eu.scene set 1')
    tick(1)
    check('execute if entity @s[tag=eu.cinematic]')
    cmd('trigger eu.scene set 2')
    tick(1)
    check('execute unless entity @s[tag=eu.cinematic]')

with tempfile.TemporaryDirectory(prefix='undertow-') as tmp:
    lab = Path(tmp)
    packs = lab/'saves/testworld/datapacks'
    packs.mkdir(parents=True)
    if args.pack_zip:
        shutil.copy2(args.pack_zip, packs/args.pack_zip.name)
    else:
        shutil.copytree(root/'datapacks/ender_undertow', packs/'ender_undertow')
    script = lab/'script.jsonl'
    output = lab/'result.jsonl'
    script.write_text(''.join(json.dumps(op)+'\n' for op in ops))
    env = dict(os.environ, HORIZON_SERVER_THREAD='1', HORIZON_PLAYER_NAME='Player',
               HORIZON_COMMAND_SCRIPT=str(script), HORIZON_COMMAND_OUTPUT=str(output),
               HORIZON_HOME=str(lab/'home'), HORIZON_LAUNCHER_HOME=str(lab/'launcher'),
               XDG_DATA_HOME=str(lab/'data'), XDG_CONFIG_HOME=str(lab/'config'),
               HORIZON_NO_AUDIO='1', HORIZON_LAUNCHER_NO_SELF_UPDATE='1')
    proc = subprocess.run([str(args.client.resolve()), '--resources', str(args.resources.resolve()),
                           '--world', 'testworld', '--seed', str(args.seed), '--radius', '3'],
                          cwd=lab, env=env, capture_output=True, timeout=150)
    if proc.returncode or not output.exists():
        raise RuntimeError(proc.stderr.decode(errors='replace')[-3500:])
    rows = [json.loads(line) for line in output.read_text(encoding='utf8').splitlines()]
    failures = []
    for index, value in expected.items():
        if [True,value] not in rows[index]['results']:
            failures.append((index, rows[index]['cmd'], value, rows[index]['results']))
    if failures:
        raise AssertionError(failures)
    print(f'{len(expected)} integrated-server checks passed')
