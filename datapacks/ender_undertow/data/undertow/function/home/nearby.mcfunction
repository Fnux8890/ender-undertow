execute unless function undertow:home/warm run return 0
execute unless block ~ ~ ~ #minecraft:beds unless data entity @s respawn{forced:1b} run return 0
execute positioned ~ ~ ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~ ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~ ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~ ~ ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~ ~ ~-1 if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~ ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~ ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~ ~-1 if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~ ~-1 if function undertow:home/valid run return run function undertow:land
execute positioned ~ ~1 ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~1 ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~1 ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~ ~1 ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~ ~1 ~-1 if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~1 ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~1 ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~1 ~-1 if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~1 ~-1 if function undertow:home/valid run return run function undertow:land
execute positioned ~ ~-1 ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~-1 ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~-1 ~ if function undertow:home/valid run return run function undertow:land
execute positioned ~ ~-1 ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~ ~-1 ~-1 if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~-1 ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~-1 ~1 if function undertow:home/valid run return run function undertow:land
execute positioned ~1 ~-1 ~-1 if function undertow:home/valid run return run function undertow:land
execute positioned ~-1 ~-1 ~-1 if function undertow:home/valid run return run function undertow:land
return 0
