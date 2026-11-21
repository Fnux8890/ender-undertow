# Roll exactly once, after the carrier and player have passed the release checks.
execute store result score @s eu.roll run random value 1..32
function undertow:route
