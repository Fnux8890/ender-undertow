execute store result score @s eu.roll run random value 0..2147483646
scoreboard players operation @s eu.roll %= #odds eu.roll
execute if score @s eu.roll matches 0 run function undertow:select
