tag @s add eu_assessed
execute store result score #rift_variant eu.roll run random value 1..64
execute if score #rift_variant eu.roll matches 1..4 run tag @s add eu_rift
execute if score #rift_variant eu.roll matches 5 run function #undertow:gentleman
