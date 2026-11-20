execute unless block ~ ~ ~ #undertow:air run return 0
execute positioned ~ ~-1 ~ if entity @e[type=minecraft:enderman,tag=eu_candidate,distance=..0.24] run scoreboard players set #clear eu.mob 1
scoreboard players remove #steps eu.mob 1
execute if score #clear eu.mob matches 0 if score #steps eu.mob matches 1.. positioned ^ ^ ^0.2 run function undertow:reach
