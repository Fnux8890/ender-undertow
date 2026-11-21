# Reject immediate fluid ingress before accepting a walkable exit.
execute if block ~ ~2 ~ minecraft:lava run return 0
execute if block ~ ~2 ~ minecraft:water run return 0
execute if block ~1 ~0 ~0 minecraft:lava run return 0
execute if block ~1 ~1 ~0 minecraft:lava run return 0
execute if block ~-1 ~0 ~0 minecraft:lava run return 0
execute if block ~-1 ~1 ~0 minecraft:lava run return 0
execute if block ~0 ~0 ~1 minecraft:lava run return 0
execute if block ~0 ~1 ~1 minecraft:lava run return 0
execute if block ~0 ~0 ~-1 minecraft:lava run return 0
execute if block ~0 ~1 ~-1 minecraft:lava run return 0
return 1
