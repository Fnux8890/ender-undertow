# This function runs synchronously at the point behind the miner.
tag @e[type=minecraft:enderman,limit=1,sort=nearest,distance=..0.95] add eu_candidate
scoreboard players set #steps eu.mob 14
scoreboard players set #clear eu.mob 0
execute at @s facing entity @e[type=minecraft:enderman,tag=eu_candidate,sort=nearest,limit=1] feet positioned ~ ~1 ~ run function undertow:reach
execute if score #clear eu.mob matches 1 at @s run function undertow:search
tag @e[type=minecraft:enderman,tag=eu_candidate,distance=..0.95] remove eu_candidate
