function undertow:clearance
execute unless score @s eu.open matches 1 run return 0
playsound minecraft:entity.enderman.teleport player @s ~ ~ ~ 0.8 0.85
tp @s ~ ~ ~
particle minecraft:portal ~ ~1 ~ 0.25 0.5 0.25 0.2 16
