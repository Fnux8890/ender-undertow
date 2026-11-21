execute unless entity @s[type=minecraft:enderman] run return 0
execute if entity @s[tag=eu_rift] run return 0
execute if items entity @s armor.head * run return 0
item replace entity @s armor.head with minecraft:paper[minecraft:item_model="ender_undertow:gentleman"]
data merge entity @s {drop_chances:{head:0.0f}}
tag @s add eu_assessed
tag @s add eu_gentleman
