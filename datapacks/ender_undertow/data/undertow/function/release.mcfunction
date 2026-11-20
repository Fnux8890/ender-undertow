scoreboard players reset @s eu.hold
function undertow:carrier
execute if dimension minecraft:overworld if entity @s[gamemode=survival] if score #near eu.mob matches 1 run function undertow:drop
