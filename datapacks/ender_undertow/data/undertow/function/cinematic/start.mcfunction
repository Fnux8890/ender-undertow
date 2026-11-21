tag @s add eu.in_scene
execute at @e[type=minecraft:enderman,tag=eu_candidate,distance=..3,sort=nearest,limit=1] run playsound minecraft:block.gravel.step hostile @s ~ ~ ~ 0.48 0.72
scoreboard players set @s eu.hold 62
tag @s remove eu.gent_scene
execute if entity @e[type=minecraft:enderman,tag=eu_candidate,tag=eu_gentleman,distance=..3] run tag @s add eu.gent_scene
execute if entity @s[tag=eu.gent_scene] run playsound ender_undertow:gentleman_apology voice @s ~ ~ ~ 1 1
execute unless entity @s[tag=eu.gent_scene] run playsound ender_undertow:stare_rise hostile @s ~ ~ ~ 0.95 1.12
data modify storage undertow:scratch grab.uuid set from entity @e[type=minecraft:enderman,tag=eu_candidate,distance=..3,sort=nearest,limit=1] UUID
function undertow:cinematic/grab with storage undertow:scratch grab
