execute unless entity @s[tag=eu.in_scene] run playsound minecraft:entity.enderman.teleport player @s ~ ~ ~ 0.8 0.85
execute store success score @s eu.open run tp @s ~ ~ ~
execute unless score @s eu.open matches 1 run return 0
execute if entity @s[tag=eu.in_scene] run function undertow:cinematic/arrived
particle minecraft:portal ~ ~1 ~ 0.25 0.5 0.25 0.2 16
return 1
