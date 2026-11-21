scoreboard players set @s eu.found 0
scoreboard players set @s eu.tries 32
execute if entity @e[type=minecraft:enderman,tag=eu_candidate,tag=eu_rift,sort=nearest,limit=1,distance=..3] run function undertow:rift/probe
execute if score @s eu.found matches 0 run scoreboard players set @s eu.tries 32
execute if score @s eu.found matches 0 run function undertow:probe
execute if score @s eu.found matches 1 if block ~-0.31 ~2 ~-0.31 #undertow:air if block ~-0.31 ~2 ~0.31 #undertow:air if block ~0.31 ~2 ~-0.31 #undertow:air if block ~0.31 ~2 ~0.31 #undertow:air run function undertow:pickup
