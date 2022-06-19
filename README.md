# OBSpawnControl
Minecraft bukkit/spigot plugin to set an override spawn for a world. Players will always spawn at that location when joining or respawning in the world.

Use the /obspawn or /obs command to set or remove a spawn.
Existing spawns are read from the plugin config at startup or as worlds are loaded as in the case of MultiVerse.

Useful for your lobby or servers where you want players to always spawn in a particular location.

Commands:
/obs list or /obs show to show current spawn locations
/obs setspawn to set the spawn at the player location for the world they are in
/obs delspawn to remove spawn and resume normal Minecraft spawning for the world the player is in

Compiled for 1.18 with Java 17, but should work with older versions (up to a point).