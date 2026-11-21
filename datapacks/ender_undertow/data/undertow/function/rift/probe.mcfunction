scoreboard players remove @s eu.tries 1
execute store result storage undertow:scratch offset.x int 1 store result score @s eu.dx run random value -128..128
execute store result storage undertow:scratch offset.z int 1 store result score @s eu.dz run random value -128..128
function undertow:rift/column with storage undertow:scratch offset
execute if score @s eu.found matches 0 if score @s eu.tries matches 1.. run function undertow:rift/probe
