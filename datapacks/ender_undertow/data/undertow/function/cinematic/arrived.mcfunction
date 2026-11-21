title @s actionbar {text:"undertow:arrive"}
execute at @s run playsound minecraft:block.stone.fall player @s ~ ~ ~ 0.42 0.88
execute at @s run playsound ender_undertow:arrival_echo player @s ~ ~ ~ 0.22 0.74
tag @s remove eu.in_scene
tag @s remove eu.gent_scene
