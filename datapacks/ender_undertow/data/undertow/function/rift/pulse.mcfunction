scoreboard players set #rift_clock eu.roll 0
execute as @a at @s if dimension minecraft:overworld as @e[type=minecraft:enderman,tag=!eu_assessed,distance=..32] run function undertow:rift/classify
execute as @a at @s if dimension minecraft:overworld at @e[type=minecraft:enderman,tag=eu_rift,distance=..32] run particle minecraft:soul_fire_flame ~ ~2.35 ~ 0.23 0.12 0.23 0.008 3 normal @s
