execute unless dimension minecraft:overworld run scoreboard players reset @s eu.hold
execute unless entity @s[gamemode=survival] run scoreboard players reset @s eu.hold
function undertow:carrier
execute unless score #near eu.mob matches 1 run scoreboard players reset @s eu.hold
execute if score @s eu.hold matches 1.. if block ~-0.31 ~2 ~-0.31 #undertow:air if block ~-0.31 ~2 ~0.31 #undertow:air if block ~0.31 ~2 ~-0.31 #undertow:air if block ~0.31 ~2 ~0.31 #undertow:air run tp @s ~ ~0.18 ~
