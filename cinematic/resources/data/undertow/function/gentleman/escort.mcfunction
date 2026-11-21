execute store result score #overlook eu.open run undertow_find_overlook
execute unless score #overlook eu.open matches 1 run return 0
return run function undertow:gentleman/visit with storage undertow:overlook
