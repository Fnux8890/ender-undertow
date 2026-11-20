execute store result storage undertow:scratch dest.x int 1 run scoreboard players get @s eu.x
execute store result storage undertow:scratch dest.y int 1 run scoreboard players get @s eu.y
execute store result storage undertow:scratch dest.z int 1 run scoreboard players get @s eu.z
execute if dimension minecraft:overworld if entity @s[gamemode=survival] run function undertow:arrive with storage undertow:scratch dest
