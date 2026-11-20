tag @s add eu.in_scene
execute at @e[type=minecraft:enderman,tag=eu_candidate,distance=..3,sort=nearest,limit=1] run playsound minecraft:block.gravel.step hostile @s ~ ~ ~ 0.48 0.72
scoreboard players set @s eu.hold 27
data modify storage undertow:scratch grab.uuid set from entity @e[type=minecraft:enderman,tag=eu_candidate,distance=..3,sort=nearest,limit=1] UUID
function undertow:cinematic/grab with storage undertow:scratch grab
