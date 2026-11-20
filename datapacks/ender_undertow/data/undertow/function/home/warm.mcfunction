# Standalone packs inspect loaded chunks. The Java mod loads this footprint on demand.
execute unless loaded ~-2 ~ ~-2 run return 0
execute unless loaded ~-2 ~ ~2 run return 0
execute unless loaded ~2 ~ ~-2 run return 0
execute unless loaded ~2 ~ ~2 run return 0
return 1
