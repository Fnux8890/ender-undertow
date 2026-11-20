scoreboard players set @s eu.open 0
# Reject immediate fluid ingress before accepting a walkable exit.
execute if block ~ ~2 ~ minecraft:lava run return 0
execute if block ~ ~2 ~ minecraft:water run return 0
execute unless loaded ~1 ~ ~0 run return 0
execute if block ~1 ~0 ~0 minecraft:lava run return 0
execute if block ~1 ~1 ~0 minecraft:lava run return 0
execute unless loaded ~-1 ~ ~0 run return 0
execute if block ~-1 ~0 ~0 minecraft:lava run return 0
execute if block ~-1 ~1 ~0 minecraft:lava run return 0
execute unless loaded ~0 ~ ~1 run return 0
execute if block ~0 ~0 ~1 minecraft:lava run return 0
execute if block ~0 ~1 ~1 minecraft:lava run return 0
execute unless loaded ~0 ~ ~-1 run return 0
execute if block ~0 ~0 ~-1 minecraft:lava run return 0
execute if block ~0 ~1 ~-1 minecraft:lava run return 0
execute positioned ~1 ~ ~0 if loaded ~ ~ ~ if block ~ ~-1 ~ #undertow:floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air run scoreboard players set @s eu.open 1
execute positioned ~-1 ~ ~0 if loaded ~ ~ ~ if block ~ ~-1 ~ #undertow:floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air run scoreboard players set @s eu.open 1
execute positioned ~0 ~ ~1 if loaded ~ ~ ~ if block ~ ~-1 ~ #undertow:floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air run scoreboard players set @s eu.open 1
execute positioned ~0 ~ ~-1 if loaded ~ ~ ~ if block ~ ~-1 ~ #undertow:floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air run scoreboard players set @s eu.open 1
