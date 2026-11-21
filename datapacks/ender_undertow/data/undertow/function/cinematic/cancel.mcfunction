execute if entity @s[tag=eu.gent_scene] run stopsound @s voice ender_undertow:gentleman_apology
stopsound @s hostile minecraft:entity.enderman.scream
stopsound @s hostile ender_undertow:stare_rise
title @s actionbar {text:"undertow:cancel"}
tag @s remove eu.in_scene
tag @s remove eu.gent_scene
