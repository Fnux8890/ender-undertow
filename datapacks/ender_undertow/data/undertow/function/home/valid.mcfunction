execute unless block ~ ~-1 ~ #undertow:home_floor run return 0
execute unless block ~ ~ ~ #undertow:air run return 0
execute unless block ~ ~1 ~ #undertow:air run return 0
execute unless function undertow:dry run return 0
execute positioned ~1 ~ ~0 if block ~ ~-1 ~ #undertow:home_floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air run return 1
execute positioned ~-1 ~ ~0 if block ~ ~-1 ~ #undertow:home_floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air run return 1
execute positioned ~0 ~ ~1 if block ~ ~-1 ~ #undertow:home_floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air run return 1
execute positioned ~0 ~ ~-1 if block ~ ~-1 ~ #undertow:home_floor if block ~ ~ ~ #undertow:air if block ~ ~1 ~ #undertow:air run return 1
return 0
