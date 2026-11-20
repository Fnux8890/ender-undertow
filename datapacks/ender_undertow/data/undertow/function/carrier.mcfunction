scoreboard players operation #wanted eu.mob = @s eu.mob
scoreboard players set #near eu.mob 0
execute as @e[type=minecraft:enderman,distance=..3.5] if score @s eu.mob = #wanted eu.mob run scoreboard players set #near eu.mob 1
