execute unless dimension minecraft:overworld run scoreboard players reset @s eu.hold
execute unless entity @s[gamemode=survival] run scoreboard players reset @s eu.hold
function undertow:carrier
execute unless score #near eu.mob matches 1 run scoreboard players reset @s eu.hold
# Advance only the selected player, keeping the lift within its original eight steps.
execute if entity @s[tag=eu.in_scene] unless score @s eu.hold matches 1.. run function undertow:cinematic/cancel
execute if entity @s[tag=eu.in_scene] if score @s eu.hold matches 12 run playsound minecraft:entity.enderman.scream hostile @s ~ ~ ~ 0.65 0.85
execute if entity @s[tag=eu.in_scene] if score @s eu.hold matches 6 run title @s actionbar {text:"undertow:close"}
execute if entity @s[tag=eu.in_scene] if score @s eu.hold matches 1 run stopsound @s hostile minecraft:entity.enderman.scream
execute if entity @s[tag=eu.in_scene] if score @s eu.hold matches 1..19 run return 0
execute if score @s eu.hold matches 1.. if block ~-0.31 ~2 ~-0.31 #undertow:air if block ~-0.31 ~2 ~0.31 #undertow:air if block ~0.31 ~2 ~-0.31 #undertow:air if block ~0.31 ~2 ~0.31 #undertow:air run tp @s ~ ~0.18 ~
