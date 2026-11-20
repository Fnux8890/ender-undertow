execute as @e[type=minecraft:enderman,tag=eu_candidate,distance=..3,sort=nearest,limit=1] unless score @s eu.mob matches 1.. run function undertow:identify
scoreboard players operation @s eu.mob = @e[type=minecraft:enderman,tag=eu_candidate,distance=..3,sort=nearest,limit=1] eu.mob
# Lift in the miner's own column so a nearby wall cannot be crossed.
scoreboard players set @s eu.hold 8
scoreboard players set @s eu.cool 2400
playsound minecraft:entity.enderman.teleport player @s ~ ~ ~ 0.4 0.6
execute if entity @s[tag=eu.cinematic] run function undertow:cinematic/start
function undertow:lift
