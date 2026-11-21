# As a selected survival player, with an Enderman immediately behind them.
# Bypass cooldown and the random roll only. Use the production selection and safe landing checks.
execute unless entity @s[type=minecraft:player,gamemode=survival] run return 0
execute unless dimension minecraft:overworld run return 0
execute if score @s eu.hold matches 0.. run return 0
execute at @s rotated ~ 0 positioned ^ ^ ^-1.3 if entity @e[type=minecraft:enderman,distance=..0.95] run function undertow:select
