scoreboard players set @s eu.found 0
scoreboard players set @s eu.tries 32
function undertow:probe
execute if score @s eu.found matches 1 if block ~-0.31 ~2 ~-0.31 #undertow:air if block ~-0.31 ~2 ~0.31 #undertow:air if block ~0.31 ~2 ~-0.31 #undertow:air if block ~0.31 ~2 ~0.31 #undertow:air run function undertow:pickup
