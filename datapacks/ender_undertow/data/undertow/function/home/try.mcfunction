execute unless data entity @s respawn{dimension:"minecraft:overworld"} run return 0
data remove storage undertow:scratch home
execute store result storage undertow:scratch home.x int 1 run data get entity @s respawn.pos[0]
execute store result storage undertow:scratch home.y int 1 run data get entity @s respawn.pos[1]
execute store result storage undertow:scratch home.z int 1 run data get entity @s respawn.pos[2]
return run function undertow:home/anchor with storage undertow:scratch home
