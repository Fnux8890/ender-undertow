tag @s add eu_assessed
execute store result score #rift_variant eu.roll run random value 1..16
execute if score #rift_variant eu.roll matches 1 run tag @s add eu_rift
