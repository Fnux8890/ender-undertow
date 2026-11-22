"""Build the Horizon world data pack and companion resources from the shared sources."""
import argparse
import json
import re
from math import isqrt
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile, ZipInfo

ROOT = Path(__file__).resolve().parents[1]
DIST = ROOT / "dist"
DIST.mkdir(exist_ok=True)
BASE = ROOT / "datapacks/ender_undertow"
JAVA = ROOT / "cinematic/resources"
PREFIX = "data/undertow/function/"
VERSION = "26.3-beta.2"


def tree(directory):
    return {p.relative_to(directory).as_posix(): p.read_bytes()
            for p in directory.rglob("*") if p.is_file() and p.name != "README.md"}


def archive(name, entries):
    path = DIST / f"ender-undertow-horizon-{name}-{VERSION}.zip"
    with ZipFile(path, "w") as output:
        for key, data in sorted(entries.items()):
            output.writestr(ZipInfo(key), data, compress_type=ZIP_DEFLATED)
    print(path)


def function(name, commands):
    data[PREFIX + name + ".mcfunction"] = ("\n".join(commands) + "\n").encode()


data = tree(BASE)
data.update({"data/" + key: value for key, value in tree(JAVA / "data").items()})
# Horizon synchronously loads a newly command-forced chunk. Take only the
# missing corners of the bed footprint, then release exactly our own tickets
# in the same function call, even when the bed or landing fails validation.
corners = [(x,z) for x in (-2,2) for z in (-2,2)]
warm = []
release = []
for i,(x,z) in enumerate(corners):
    ticket = f"#home_ticket{i} eu.open"
    warm += [f"scoreboard players set {ticket} 0",
             f"execute unless loaded ~{x} ~ ~{z} store success score {ticket} run forceload add ~{x} ~{z}"]
    release.append(f"execute if score {ticket} matches 1 run forceload remove ~{x} ~{z}")
function("horizon/home", warm + ["execute store result score #home_result eu.open run function undertow:home/nearby"]
         + release + ["return run scoreboard players get #home_result eu.open"])
# The nightly updates the ticking flag on its next tick. A newly forced
# chunk already has readable blocks even while "if loaded" is still false.
footprint = []
for i,(x,z) in enumerate(corners):
    function(f"horizon/ticket_{i}", [f"return run forceload query ~{x} ~{z}"])
    footprint.append(f"execute unless loaded ~{x} ~ ~{z} unless function undertow:horizon/ticket_{i} run return 0")
function("home/warm", footprint + ["return 1"])
anchor = PREFIX + "home/anchor.mcfunction"
data[anchor] = data[anchor].replace(b"undertow:home/nearby", b"undertow:horizon/home")

# Survey two X slices per tick during the hold. Each (x,z) column shares a
# vertical function with columns of the same inner and outer sphere radii.
# Only the rare Gentleman performs this work, in already-loaded chunks.
vertical = set()
order = [0] + [s*i for i in range(1,49) for s in (1,-1)]
for x in range(49):
    lines = []
    for z in order:
        horizontal = x*x + z*z
        if horizontal > 48*48:
            continue
        high = isqrt(48*48-horizontal)
        low = isqrt(143-horizontal)+1 if horizontal < 144 else 0
        vertical.add((low, high))
        lines.append(f"execute positioned ~ ~ ~{z} if loaded ~ ~ ~ if function undertow:horizon/column/{low}_{high} run return 1")
    function(f"horizon/slice/{x}", lines + ["return 0"])
for low, high in sorted(vertical):
    lines = []
    for y in order:
        if low <= abs(y) <= high:
            lines.append(f"execute positioned ~ ~{y} ~ if block ~ ~ ~ #minecraft:diamond_ores if function undertow:horizon/remember run return 1")
    function(f"horizon/column/{low}_{high}", lines + ["return 0"])
schedule = []
for i in range(0,len(order),2):
    name = f"horizon/pair/{i//2}"
    commands = [f"$execute positioned $(x) $(y) $(z) positioned ~{dx} ~ ~ if function undertow:horizon/slice/{abs(dx)} run return 1"
                for dx in order[i:i+2]]
    function(name, commands + ["return 0"])
    schedule.append(f"execute if score @s eu.hold matches {61-i//2} run return run function undertow:{name} with storage undertow:survey")
function("horizon/survey", [f"execute store result storage undertow:survey {a} int 1 run scoreboard players get @s eu.src{a}" for a in "xyz"] + schedule)
function("horizon/save_position", ["data modify storage undertow:survey pos set from entity @s Pos", "kill @s"])
function("horizon/remember", ["execute unless function undertow:horizon/inspect run return 0",
                              "execute summon minecraft:marker run function undertow:horizon/save_position"] +
         [f"execute store result score @s eu.ore{a} run data get storage undertow:survey pos[{i}]" for i,a in enumerate("xyz")] +
         ["scoreboard players set @s eu.vein 1","return 1"])
function("gentleman/escort", ["execute unless dimension minecraft:overworld run return 0",
                              "execute unless entity @s[gamemode=survival] run return 0",
                              "execute unless score @s eu.vein matches 1 run return 0"] +
         [f"execute store result storage undertow:survey {a} int 1 run scoreboard players get @s eu.ore{a}" for a in "xyz"] +
         ["return run function undertow:horizon/visit with storage undertow:survey"])
function("horizon/visit", ["$execute positioned $(x) $(y) $(z) if loaded ~ ~ ~ if block ~ ~ ~ #minecraft:diamond_ores run return run function undertow:horizon/overlook", "return 0"])
# Stand three blocks from an exposed face. Clear a sightline at eye height.
# blink repeats the normal floor, headroom, lava and escape checks at release.
approaches = []
inspection = []
for i, (x, z) in enumerate([(1, 0), (-1, 0), (0, 1), (0, -1)]):
    approach = f"positioned ~{3*x} ~-1 ~{3*z} align xyz positioned ~0.5 ~ ~0.5"
    sight = " ".join(f"if loaded ~{x*d} ~ ~{z*d} if block ~{x*d} ~ ~{z*d} #undertow:air" for d in (1, 2))
    inspection.append(f"execute {sight} {approach} run function undertow:clearance")
    inspection.append("execute if score @s eu.open matches 1 run return 1")
    approaches.append(f"execute {sight} {approach} if function undertow:blink run return run function undertow:horizon/face_{i}")
    # The command source here is the landing position, even after blink.
    function(f"horizon/face_{i}", [f"rotate @s facing ~{-3*x} ~1.5 ~{-3*z}", "return 1"])
function("horizon/inspect", ["scoreboard players set @s eu.open 0"] + inspection + ["return 0"])
function("horizon/overlook", approaches + ["return 0"])

# Use a target-only system message to reach Horizon's cancellable chat hook.
# Ordinary title-actionbar packets expose their type but not their contents.
for key, value in list(data.items()):
    if key.endswith(".mcfunction"):
        data[key] = value.replace(b"title @s actionbar ", b"tellraw @s ")
start = PREFIX + "cinematic/start.mcfunction"
read_carrier = b"data modify storage undertow:scratch grab.pos set from entity @e[type=minecraft:enderman,tag=eu_candidate,distance=..3,sort=nearest,limit=1] Pos"
data[start] = data[start].replace(b"function undertow:cinematic/grab", read_carrier + b"\nfunction undertow:cinematic/grab")
key = PREFIX + "cinematic/grab.mcfunction"
data[key] = data[key].replace(b"$(uuid)", b"$(uuid)|$(pos)")
load_key = PREFIX + "load.mcfunction"
tick_key = PREFIX + "tick.mcfunction"
data[load_key] += b"\nscoreboard objectives add eu.scene trigger\nscoreboard objectives add eu.left minecraft.custom:minecraft.leave_game\n"
data[tick_key] = (b"tag @a[scores={eu.left=1..}] remove eu.cinematic\n"
                  b"scoreboard players reset @a[scores={eu.left=1..}] eu.left\n"
                  b"tag @a[scores={eu.scene=1}] add eu.cinematic\n"
                  b"tag @a[scores={eu.scene=2}] remove eu.cinematic\n"
                  b"scoreboard players reset @a[scores={eu.scene=1..}] eu.scene\n"
                  b"scoreboard players enable @a eu.scene\n"
                  + data[tick_key])
function("horizon/begin", ["scoreboard players set @s eu.vein 0"] +
         [f"execute store result score @s eu.src{a} run data get entity @s Pos[{i}]" for i,a in enumerate("xyz")])
data[start] += b"\nexecute if entity @s[tag=eu.gent_scene] run function undertow:horizon/begin\n"
for score in ["eu.vein"] + ["eu."+stem+a for stem in ("src","ore") for a in "xyz"]:
    data[load_key] += f"scoreboard objectives add {score} dummy\n".encode()
lift = PREFIX + "lift.mcfunction"
before = b"execute if entity @s[tag=eu.in_scene] if score @s eu.hold matches 1..54"
assert before in data[lift]
data[lift] = data[lift].replace(before, b"execute if entity @s[tag=eu.gent_scene,tag=eu.in_scene] if score @s eu.hold matches 13..61 unless score @s eu.vein matches 1 run function undertow:horizon/survey\n"+before)
# On this nightly command parsing precedes the world's custom block-tag load.
# Resolve pack-owned tags at execution time, retaining modpack tag overrides.
checks = {}
block_clause = re.compile(r"(if|unless) block (~[.0-9-]*) (~[.0-9-]*) (~[.0-9-]*) #undertow:(air|floor|home_floor)\b")


def runtime_block(match):
    condition, x, y, z, tag = match.groups()
    position = f"{x} {y} {z}"
    key = (tag, position)
    if key not in checks:
        name = f"horizon/check/{tag}_{len(checks)}"
        checks[key] = name
        function(name, [f"execute positioned {position} if predicate undertow:horizon/{tag} run return 1", "return 0"])
    return f"{condition} function undertow:{checks[key]}"


for tag in ("air", "floor", "home_floor"):
    predicate = {"type": "minecraft:location_check", "predicate": {"block": {"blocks": "#undertow:" + tag}}}
    data[f"data/undertow/predicate/horizon/{tag}.json"] = json.dumps(predicate).encode()
for key, value in list(data.items()):
    if key.endswith(".mcfunction") and b"#undertow:" in value:
        data[key] = block_clause.sub(runtime_block, value.decode()).encode()
archive("data", data)
assets = {"assets/" + key: value for key, value in tree(JAVA / "assets").items()
          if "/shaders/" not in key}
assets["pack.mcmeta"] = json.dumps({"pack": {"description": "Ender Undertow voices and wardrobe",
                                             "min_format": [97, 1], "max_format": [97, 1]}}).encode()
assets["pack.png"] = (ROOT / "art/ender_undertow.png").read_bytes()
assets["LICENSE"] = (ROOT / "LICENSE").read_bytes()
assets["VOICE-CREDITS.md"] = (ROOT / "cinematic/VOICE-CREDITS.md").read_bytes()
archive("resources", assets)

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("--native", type=Path, help="Packaged Horizon .hmod to include in the installation bundle")
parser.add_argument("--shader", type=Path, help="Validated Horizon .hshader to include in the installation bundle")
args = parser.parse_args()
if bool(args.native) != bool(args.shader):
    parser.error("--native and --shader must be supplied together")
if args.native:
    for package in (args.native, args.shader):
        with ZipFile(package) as packaged:
            if "LICENSE" not in packaged.namelist() or (
                packaged.read("LICENSE").decode().splitlines()
                != (ROOT / "LICENSE").read_text(encoding="utf-8").splitlines()
            ):
                parser.error(f"{package.name} must include the full project LICENSE")
    bundle = {
        "INSTALL.md": (ROOT / "horizon/README.md").read_bytes(),
        "LICENSE": (ROOT / "LICENSE").read_bytes(),
        "ender_undertow_cinematic.hmod": args.native.read_bytes(),
        "ender_undertow_focus.hshader": args.shader.read_bytes(),
    }
    for kind in ("data", "resources"):
        archive_path = DIST / f"ender-undertow-horizon-{kind}-{VERSION}.zip"
        bundle[archive_path.name] = archive_path.read_bytes()
    archive("bundle", bundle)
