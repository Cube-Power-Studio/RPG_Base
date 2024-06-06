package rpg.rpg_base.IslandManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.data.Util.Util;

import java.util.*;

public class Island {
    RPG_Base plugin;
    private int minX;
    private int minZ;
    private int minProtectedX;
    private int minProtectedZ;
    private int protectionRange;
    private int y;
    private Location center;
    private World world;
    private UUID owner;
    private long createdDate;
    private long updatedDate;
    private int islandDistance;
    private boolean locked = false;
    private boolean isSpawn;
    private boolean purgeProtected;
    private Location spawnPoint;
    private Biome biome;
    private static final List<String> islandSettingsKey = new ArrayList<>();

    static {
        islandSettingsKey.clear();
        islandSettingsKey.add("");
    }

    private final HashMap<SettingsFlag, Boolean> igs = new HashMap<>();

    public enum SettingsFlag {
        ANVIL,
        BEACON,
        BED,
        BREAK_BLOCKS,
        BREEDING,
        BREWING,
        BUCKET,
        COLLECT_FLUIDS,
        CHEST,
        CRAFTING,
        CROP_TRAMPLE,
        CREEPER_PAIN,
        DOOR,
        EGGS,
        ENCHANTING,
        ENDER_PEARL,
        FIRE,
        ENTER_EXIT_MESSAGES,
        FIRE_EXTINGUISH,
        FIRE_SPREAD,
        FURNACE,
        GATE,
        HURT_ENTITIES,
        MILKING,
        PVP,
        MOB_SPAWN,
        MONSTER_SPAWN,
        PLACE_BLOCKS,
        REDSTONE,
        SPAWN_EGGS,
        SHEARING,
        TRADING,
        VISITOR_ITEM_DROP,
        VISITOR_ITEM_PICKUP
    }

    public Island(RPG_Base plugin, String serial, List<String> settingsKey) {
        this.plugin = plugin;
        // Bukkit.getLogger().info("DEBUG: adding serialized island to grid ");
        // Deserialize
        // Format:
        // x:height:z:protection range:island distance:owner UUID: locked: protected
        String[] split = serial.split(":");
        try {
            protectionRange = Integer.parseInt(split[3]);
            islandDistance = Integer.parseInt(split[4]);
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[2]);
            minX = x - islandDistance / 2;
            y = Integer.parseInt(split[1]);
            minZ = z - islandDistance / 2;
            minProtectedX = x - protectionRange / 2;
            minProtectedZ = z - protectionRange / 2;
            this.world = RPG_Base.getIslandWorld();
            this.center = new Location(world, x, y, z);
            this.createdDate = new Date().getTime();
            this.updatedDate = createdDate;
            if (split.length > 6) {
                this.locked = split[6].equalsIgnoreCase("true");
            } else {
                this.locked = false;
            }
            if (split.length > 7) {
                this.purgeProtected = split[7].equalsIgnoreCase("true");
            } else {
                this.purgeProtected = false;
            }
            if (!split[5].equals("null")) {
                if (split[5].equals("spawn")) {
                    isSpawn = true;

                    if (split.length > 8) {
                        spawnPoint = Util.getLocationString(serial.substring(serial.indexOf(":SP:") + 4));
                    }
                } else {
                    owner = UUID.fromString(split[5]);
                }
            }
            if (split.length > 8) {
                setSettings(split[8], settingsKey);
            } else {
                setSettings(null, settingsKey);
            }
            if (split.length > 9) {
                try {
                    biome = Biome.valueOf(split[9]);

                } catch (IllegalArgumentException ee) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIgsDefaults() {
        for (SettingsFlag flag : SettingsFlag.values()) {
            if (!Settings.defaultIslandSettings.containsKey(flag)) {
                // Default default
                if (flag.equals(SettingsFlag.MOB_SPAWN) || flag.equals(SettingsFlag.MONSTER_SPAWN)) {
                    this.igs.put(flag, true);
                } else {
                    this.igs.put(flag, false);
                }
            } else {
                if (Settings.defaultIslandSettings.get(flag) == null) {
                    //plugin.getLogger().info("DEBUG: null flag " + flag);
                    if (flag.equals(SettingsFlag.MOB_SPAWN) || flag.equals(SettingsFlag.MONSTER_SPAWN)) {
                        this.igs.put(flag, true);
                    } else {
                        this.igs.put(flag, false);
                    }
                } else {
                    this.igs.put(flag, Settings.defaultIslandSettings.get(flag));
                }
            }
        }
    }

    public void setSpawnDefaults() {
        for (SettingsFlag flag : SettingsFlag.values()) {
            if (!Settings.defaultSpawnSettings.containsKey(flag)) {
                // Default default
                if (flag.equals(SettingsFlag.MOB_SPAWN) || flag.equals(SettingsFlag.MONSTER_SPAWN)) {
                    this.igs.put(flag, true);
                } else {
                    this.igs.put(flag, false);
                }
            } else {
                if (Settings.defaultSpawnSettings.get(flag) == null) {
                    if (flag.equals(SettingsFlag.MOB_SPAWN) || flag.equals(SettingsFlag.MONSTER_SPAWN)) {
                        this.igs.put(flag, true);
                    } else {
                        this.igs.put(flag, false);
                    }
                } else {
                    this.igs.put(flag, Settings.defaultSpawnSettings.get(flag));
                }
            }
        }
    }
    public Island(RPG_Base plugin, int x, int z) {
        this(plugin, x, z, null);
    }

    public Island(RPG_Base plugin, int x, int z, UUID owner) {
        this.plugin = plugin;
        // Calculate min minX and z
        this.minX = x - Settings.islandDistance / 2;
        this.minZ = z - Settings.islandDistance / 2;
        this.minProtectedX = x - Settings.islandProtectionRange / 2;
        this.minProtectedZ = z - Settings.islandProtectionRange / 2;
        this.y = Settings.islandHeight;
        this.islandDistance = Settings.islandDistance;
        this.protectionRange = Settings.islandProtectionRange;
        this.world = RPG_Base.getIslandWorld();
        this.center = new Location(world, x, y, z);
        this.createdDate = new Date().getTime();
        this.updatedDate = createdDate;
        this.owner = owner;
        // Island Guard Settings
        setIgsDefaults();
    }

    public Island(Island island) {
        this.plugin = island.plugin;
        this.biome = island.biome == null ? null : Biome.valueOf(island.biome.name());
        this.center = island.center != null ? island.center.clone() : null;
        this.createdDate = Long.valueOf(island.createdDate);
        island.igs.forEach((k, v) -> this.igs.put(k, v));
        this.islandDistance = Integer.valueOf(island.islandDistance);
        this.isSpawn = Boolean.valueOf(island.isSpawn);
        this.locked = Boolean.valueOf(island.locked);
        this.minProtectedX = Integer.valueOf(island.minProtectedX);
        this.minProtectedZ = Integer.valueOf(island.minProtectedZ);
        this.minX = Integer.valueOf(island.minX);
        this.minZ = Integer.valueOf(island.minZ);
        this.owner = island.owner == null ? null : UUID.fromString(island.owner.toString());
        this.protectionRange = Integer.valueOf(island.protectionRange);
        this.purgeProtected = Boolean.valueOf(island.purgeProtected);
        this.spawnPoint = island.spawnPoint == null ? null : island.spawnPoint.clone();
        this.updatedDate = Long.valueOf(island.updatedDate);
        this.world = island.world == null ? null : Bukkit.getWorld(island.world.getUID());
        this.y = Integer.valueOf(island.y);
    }

    public boolean onIsland(Location target) {
        if (world != null) {
            // If the new nether is being used, islands exist in the nether too
            if (target.getWorld().equals(world)) {
                return target.getBlockX() >= minProtectedX && target.getBlockX() < (minProtectedX
                        + protectionRange)
                        && target.getBlockZ() >= minProtectedZ && target.getBlockZ() < (minProtectedZ
                        + protectionRange);
            }
        }
        return false;
    }

    public boolean inIslandSpace(Location target) {
        if (target.getWorld().equals(RPG_Base.getIslandWorld())) {
            return target.getX() >= center.getBlockX() - islandDistance / 2
                    && target.getX() < center.getBlockX() + islandDistance / 2
                    && target.getZ() >= center.getBlockZ() - islandDistance / 2
                    && target.getZ() < center.getBlockZ() + islandDistance / 2;
        }
        return false;
    }

    public boolean inIslandSpace(int x, int z) {
        return x >= center.getBlockX() - islandDistance / 2
                && x < center.getBlockX() + islandDistance / 2
                && z >= center.getBlockZ() - islandDistance / 2
                && z < center.getBlockZ() + islandDistance / 2;
    }

    /**
     * @return the minX
     */
    public int getMinX() {
        return minX;
    }

    /**
     * @param minX the minX to set
     */
    public void setMinX(int minX) {
        this.minX = minX;
    }

    /**
     * @return the z
     */
    public int getMinZ() {
        return minZ;
    }

    /**
     * @param minZ the z to set
     */
    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    /**
     * @return the minprotectedX
     */
    public int getMinProtectedX() {
        return minProtectedX;
    }

    /**
     * @return the minProtectedZ
     */
    public int getMinProtectedZ() {
        return minProtectedZ;
    }

    /**
     * @return the protectionRange
     */
    public int getProtectionSize() {
        return protectionRange;
    }

    /**
     * @param protectionSize the protectionSize to set
     */
    public void setProtectionSize(int protectionSize) {
        this.protectionRange = protectionSize;
        this.minProtectedX = center.getBlockX() - protectionSize / 2;
        this.minProtectedZ = center.getBlockZ() - protectionSize / 2;

    }

    /**
     * @return the islandDistance
     */
    public int getIslandDistance() {
        return islandDistance;
    }

    /**
     * @param islandDistance the islandDistance to set
     */
    public void setIslandDistance(int islandDistance) {
        this.islandDistance = islandDistance;
    }

    /**
     * @return the center
     */
    public Location getCenter() {
        return center;
    }

    /**
     * @param center the center to set
     */
    public void setCenter(Location center) {
        this.center = center;
    }

    /**
     * @return the owner
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(UUID owner) {
        this.owner = owner;
        //if (owner == null) {
        //    Bukkit.getLogger().info("DEBUG: island owner set to null for " + center);
        //}
    }

    /**
     * @return the createdDate
     */
    public long getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the updatedDate
     */
    public long getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }
    /**
     * @return the locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked
     *            the locked to set
     */
    public void setLocked(boolean locked) {
        // Bukkit.getLogger().info("DEBUG: island is now " + locked);
        this.locked = locked;
    }

    /**
     * Serializes the island for island.yml storage
     * @return string that represents the island settings
     */
    public String save() {
        // x:height:z:protection range:island distance:owner UUID
        String ownerString = "null";
        if (isSpawn) {
            // Bukkit.getLogger().info("DEBUG: island is spawn");
            ownerString = "spawn";
            if (spawnPoint != null) {
                return center.getBlockX() + ":" + center.getBlockY() + ":" + center.getBlockZ() + ":" + protectionRange + ":"
                        + islandDistance + ":" + ownerString + ":" + locked + ":" + purgeProtected + ":SP:" + Util.getStringLocation(spawnPoint);
            }
            return center.getBlockX() + ":" + center.getBlockY() + ":" + center.getBlockZ() + ":" + protectionRange + ":"
                    + islandDistance + ":" + ownerString + ":" + locked + ":" + purgeProtected;
        }
        // Not spawn
        if (owner != null) {
            ownerString = owner.toString();
        }

        return center.getBlockX() + ":" + center.getBlockY() + ":" + center.getBlockZ() + ":" + protectionRange + ":"
                + islandDistance + ":" + ownerString + ":" + locked + ":" + purgeProtected + ":" + getSettings() + ":" + getBiome().toString() + ":";
    }

    /**
     * @return Serialized set of settings
     */
    public String getSettings() {
        String result = "";
        // Personal island protection settings - serialize enum into 1's and 0's representing the boolean values
        //plugin.getLogger().info("DEBUG: igs = " + igs.toString());
        try {
            for (SettingsFlag f: SettingsFlag.values()) {
                //plugin.getLogger().info("DEBUG: flag f = " + f);
                if (this.igs.containsKey(f)) {
                    //plugin.getLogger().info("DEBUG: contains key");
                    result += this.igs.get(f) ? "1" : "0";
                } else {
                    //plugin.getLogger().info("DEBUG: does not contain key");
                    result += "0";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }
    /**
     * Get the Island Guard flag status
     * @param flag - settings flag to check
     * @return true or false, or false if flag is not in the list
     */
    public boolean getIgsFlag(SettingsFlag flag) {
        //plugin.getLogger().info("DEBUG: asking for " + flag + " = " + igs.get(flag));
        if (this.igs.containsKey(flag)) {
            return igs.get(flag);
        }
        return false;
    }

    /**
     * Set the Island Guard flag
     * @param flag - settings flag to check
     * @param value - value to set true or false
     */
    public void setIgsFlag(SettingsFlag flag, boolean value) {
        this.igs.put(flag, value);
    }

    /**
     * Provides a list of all the players who are allowed on this island
     * including coop members
     *
     * @return a list of UUIDs that have legitimate access to the island
     */
    public List<UUID> getMembers() {
        List<UUID> result = new ArrayList<UUID>();
        // Add any coop members for this island
        result.addAll(CoopPlay.getInstance().getCoopPlayers(center.toVector().toLocation(RPG_Base.getIslandWorld())));
        if (owner == null) {
            return result;
        }
        result.add(owner);
        // Add any team members
        result.addAll(plugin.getPlayers().getMembers(owner));
        return result;
    }

    /**
     * @return the isSpawn
     */
    public boolean isSpawn() {
        return isSpawn;
    }

    /**
     * @param isSpawn
     *            the isSpawn to set
     */
    public void setSpawn(boolean isSpawn) {
        this.isSpawn = isSpawn;
    }

    /**
     * @return the islandDeletable
     */
    public boolean isPurgeProtected() {
        return purgeProtected;
    }

    /**
     * @param purgeProtected the islandDeletable to set
     */
    public void setPurgeProtected(boolean purgeProtected) {
        this.purgeProtected = purgeProtected;
    }
    public void setSpawnPoint(Location location) {
        spawnPoint = location;
    }
    public Location getSpawnPoint() {
        return spawnPoint;
    }
    public void toggleIgs(SettingsFlag flag) {
        if (igs.containsKey(flag)) {
            igs.put(flag, !igs.get(flag));
        }
    }
    public Biome getBiome() {
        if (biome == null) {
            biome = center.getBlock().getBiome();
        }
        return biome;
    }
    public void setBiome(Biome biome) {
        this.biome = biome;
    }
    public void setSettings(String settings, List<String> settingsKey) {

        // Start with defaults
        if (isSpawn) {
            setSpawnDefaults();
        } else {
            setIgsDefaults();
        }
        if(settings == null || settings.isEmpty())
            return;
        if (settingsKey.size() != settings.length()) {
            plugin.getLogger().severe("Island settings does not match settings key in islands.yml. Using defaults.");
            return;
        }
        for (int i = 0; i < settingsKey.size(); i++) {
            try {
                if (settings.charAt(i) == '0') {
                    this.setIgsFlag(SettingsFlag.valueOf(settingsKey.get(i)), false);
                } else {
                    this.setIgsFlag(SettingsFlag.valueOf(settingsKey.get(i)), true);
                }
            } catch (Exception e) {
                // do nothing - bad value, probably a downgrade
            }
        }

    }
}
