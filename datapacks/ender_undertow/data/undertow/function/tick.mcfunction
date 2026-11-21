scoreboard players add #rift_clock eu.roll 1
execute if score #rift_clock eu.roll matches 16.. run function undertow:rift/pulse
scoreboard players remove @a[scores={eu.cool=1..}] eu.cool 1
scoreboard players remove @a[scores={eu.hold=1..}] eu.hold 1
execute as @a[scores={eu.hold=1..}] at @s run function undertow:lift
execute as @a[scores={eu.hold=0}] at @s run function undertow:release
tag @a[scores={eu.mine0=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine0=1..}] eu.mine0
tag @a[scores={eu.mine1=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine1=1..}] eu.mine1
tag @a[scores={eu.mine2=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine2=1..}] eu.mine2
tag @a[scores={eu.mine3=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine3=1..}] eu.mine3
tag @a[scores={eu.mine4=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine4=1..}] eu.mine4
tag @a[scores={eu.mine5=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine5=1..}] eu.mine5
tag @a[scores={eu.mine6=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine6=1..}] eu.mine6
tag @a[scores={eu.mine7=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine7=1..}] eu.mine7
tag @a[scores={eu.mine8=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine8=1..}] eu.mine8
tag @a[scores={eu.mine9=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine9=1..}] eu.mine9
tag @a[scores={eu.mine10=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine10=1..}] eu.mine10
tag @a[scores={eu.mine11=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine11=1..}] eu.mine11
tag @a[scores={eu.mine12=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine12=1..}] eu.mine12
tag @a[scores={eu.mine13=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine13=1..}] eu.mine13
tag @a[scores={eu.mine14=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine14=1..}] eu.mine14
tag @a[scores={eu.mine15=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine15=1..}] eu.mine15
tag @a[scores={eu.mine16=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine16=1..}] eu.mine16
tag @a[scores={eu.mine17=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine17=1..}] eu.mine17
tag @a[scores={eu.mine18=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine18=1..}] eu.mine18
tag @a[scores={eu.mine19=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine19=1..}] eu.mine19
tag @a[scores={eu.mine20=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine20=1..}] eu.mine20
tag @a[scores={eu.mine21=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine21=1..}] eu.mine21
tag @a[scores={eu.mine22=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine22=1..}] eu.mine22
tag @a[scores={eu.mine23=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine23=1..}] eu.mine23
tag @a[scores={eu.mine24=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine24=1..}] eu.mine24
tag @a[scores={eu.mine25=1..}] add eu_mined
scoreboard players reset @a[scores={eu.mine25=1..}] eu.mine25
execute as @a[tag=eu_mined,gamemode=survival] at @s if dimension minecraft:overworld unless score @s eu.cool matches 1.. positioned over ocean_floor positioned ~ ~-5 ~ if entity @s[dy=-384] at @s rotated ~ 0 positioned ^ ^ ^-0.65 if block ~ ~1 ~ #undertow:air positioned ^ ^ ^-0.65 if entity @e[type=minecraft:enderman,distance=..0.95] run function undertow:chance
tag @a[tag=eu_mined] remove eu_mined
