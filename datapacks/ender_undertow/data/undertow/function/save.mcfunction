function undertow:clearance
execute unless score @s eu.open matches 1 run return 0
execute store result score @s eu.x run data get entity @s Pos[0] 1
scoreboard players operation @s eu.x += @s eu.dx
execute store result score @s eu.y run data get entity @s Pos[1] 1
scoreboard players operation @s eu.y += @s eu.dy
execute store result score @s eu.z run data get entity @s Pos[2] 1
scoreboard players operation @s eu.z += @s eu.dz
scoreboard players set @s eu.found 1
