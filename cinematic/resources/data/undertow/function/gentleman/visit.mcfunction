$execute positioned $(x) $(y) $(z) align xyz positioned ~0.5 ~ ~0.5 unless loaded ~ ~ ~ run return 0
$execute positioned $(x) $(y) $(z) align xyz positioned ~0.5 ~ ~0.5 store result score #landed eu.open run function undertow:blink
execute unless score #landed eu.open matches 1 run return 0
$rotate @s facing $(ox) $(oy) $(oz)
return 1
