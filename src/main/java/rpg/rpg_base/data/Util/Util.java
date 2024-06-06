/*******************************************************************************
 * This file is part of RPG_Base.
 *
 *     RPG_Base is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     RPG_Base is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with RPG_Base.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package rpg.rpg_base.data.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import rpg.rpg_base.IslandManager.Settings;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.data.PendingItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * A set of utility methods
 *
 * @author tastybento
 *
 */
public final class Util {
    static RPG_Base plugin;
    private Util(RPG_Base plugin) {
        this.plugin = plugin;
    }
    private static final long TIMEOUT = 3000; // 3 seconds
    private static Long x = System.nanoTime();
    private static boolean midSave = false;
    private static Queue<PendingItem> saveQueue = new ConcurrentLinkedQueue<>();
    private static BukkitTask queueSaver;
    private static boolean midLoad = false;
    /**
     * Converts block face direction to radial degrees. Returns 0 if block face
     * is not radial.
     *
     * @param face
     * @return degrees
     */
    public static float blockFaceToFloat(BlockFace face) {
        switch (face) {
            case EAST:
                return 90F;
            case EAST_NORTH_EAST:
                return 67.5F;
            case EAST_SOUTH_EAST:
                return 0F;
            case NORTH:
                return 0F;
            case NORTH_EAST:
                return 45F;
            case NORTH_NORTH_EAST:
                return 22.5F;
            case NORTH_NORTH_WEST:
                return 337.5F;
            case NORTH_WEST:
                return 315F;
            case SOUTH:
                return 180F;
            case SOUTH_EAST:
                return 135F;
            case SOUTH_SOUTH_EAST:
                return 157.5F;
            case SOUTH_SOUTH_WEST:
                return 202.5F;
            case SOUTH_WEST:
                return 225F;
            case WEST:
                return 270F;
            case WEST_NORTH_WEST:
                return 292.5F;
            case WEST_SOUTH_WEST:
                return 247.5F;
            default:
                return 0F;
        }
    }
    public static void sendMessage(Player player, String message){
        player.sendMessage(message);
    }
    public static void runCommand(final Player player, final String string) {
        if (plugin.getServer().isPrimaryThread()) {
            player.performCommand(string);
        } else {
            plugin.getServer().getScheduler().runTask(plugin, () -> player.performCommand(string));
        }
    }

    /**
     * Converts a serialized location to a Location. Returns null if string is
     * empty
     *
     * @param s
     *            - serialized location in format "world:x:y:z"
     * @return Location
     */
    static public Location getLocationString(final String s) {
        if (s == null || s.trim() == "") {
            return null;
        }
        final String[] parts = s.split(":");
        if (parts.length == 4) {
            final World w = Bukkit.getServer().getWorld(parts[0]);
            if (w == null) {
                return null;
            }
            final int x = Integer.parseInt(parts[1]);
            final int y = Integer.parseInt(parts[2]);
            final int z = Integer.parseInt(parts[3]);
            return new Location(w, x, y, z);
        } else if (parts.length == 6) {
            final World w = Bukkit.getServer().getWorld(parts[0]);
            if (w == null) {
                return null;
            }
            final int x = Integer.parseInt(parts[1]);
            final int y = Integer.parseInt(parts[2]);
            final int z = Integer.parseInt(parts[3]);
            final float yaw = Float.intBitsToFloat(Integer.parseInt(parts[4]));
            final float pitch = Float.intBitsToFloat(Integer.parseInt(parts[5]));
            return new Location(w, x, y, z, yaw, pitch);
        }
        return null;
    }
    public static void sendEnterExit(Player player, String message) {
        if (!Settings.showInActionBar
                || plugin.getServer().getVersion().contains("(MC: 1.7")
                || plugin.getServer().getVersion().contains("(MC: 1.8")
                || plugin.getServer().getVersion().contains("(MC: 1.9")
                || plugin.getServer().getVersion().contains("(MC: 1.10")) {
            sendMessage(player, message);
            return;
        }
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                "minecraft:title " + player.getName() + " actionbar {\"text\":\"" + ChatColor.stripColor(message) + "\"}");
    }

    /**
     * Converts a location to a simple string representation
     * If location is null, returns empty string
     *
     * @param location
     * @return String of location
     */
    static public String getStringLocation(final Location location) {
        if (location == null || location.getWorld() == null) {
            return "";
        }
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + Float.floatToIntBits(location.getYaw()) + ":" + Float.floatToIntBits(location.getPitch());
    }
    public static YamlConfiguration loadYamlFile(String file) {
        File dataFolder = RPG_Base.getInstance().getDataFolder();
        File yamlFile = new File(dataFolder, file);

        YamlConfiguration config = null;
        if (yamlFile.exists()) {
            // Set midLoad flag to pause any saving
            midLoad = true;
            // Block until saving is paused or until a timeout, just to prevent infinite loop
            long watchdog = System.currentTimeMillis();
            while(midSave && System.currentTimeMillis() < watchdog + TIMEOUT ) {};
            try {
                config = new YamlConfiguration();
                config.load(yamlFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            midLoad = false;
        } else {
            // Create the missing file
            config = new YamlConfiguration();
            if (!file.startsWith("players")) {
                RPG_Base.getInstance().getLogger().info("No " + file + " found. Creating it...");
            }
            try {
                if (RPG_Base.getInstance().getResource(file) != null) {
                    RPG_Base.getInstance().getLogger().info("Using default found in jar file.");
                    RPG_Base.getInstance().saveResource(file, false);
                    config = new YamlConfiguration();
                    config.load(yamlFile);
                } else {
                    config.save(yamlFile);
                }
            } catch (Exception e) {
                RPG_Base.getInstance().getLogger().severe("Could not create the " + file + " file!");
            }
        }
        return config;
    }
    private static void save(YamlConfiguration yamlFile, String fileLocation, boolean async) {
        File dataFolder = RPG_Base.getInstance().getDataFolder();
        File file = new File(dataFolder, fileLocation);
        try {
            File tmpFile = File.createTempFile("yaml", null, dataFolder);
            tmpFile.deleteOnExit();
            yamlFile.save(tmpFile);
            if (tmpFile.exists()) {
                if (async) {
                    saveQueue.add(new PendingItem(tmpFile.toPath(), file.toPath()));
                } else {
                    Files.copy(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.delete(tmpFile.toPath());
                }
            }
        } catch (Exception e) {
            RPG_Base.getInstance().getLogger().severe(() -> "Could not save YAML file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves a YAML file
     *
     * @param yamlFile
     * @param fileLocation
     * @param async
     */
    public static void saveYamlFile(YamlConfiguration yamlFile, String fileLocation, boolean async) {
        async = false; // disable async for now. If you are programmer you can remove this in you own branch if you think it's okay.
        if (async) {
            if (queueSaver == null) {
                queueSaver = Bukkit.getScheduler().runTaskTimerAsynchronously(RPG_Base.getInstance(), () -> {
                    if (!RPG_Base.getInstance().isEnabled()) {
                        // Stop task if RPG_Base.getInstance() is disabled
                        queueSaver.cancel();
                    } else if (!midLoad && !midSave && !saveQueue.isEmpty()) {
                        PendingItem item = saveQueue.poll();
                        if (item != null) {
                            // Set semaphore
                            midSave = true;
                            try {
                                Files.copy(item.getSource(), item.getDest(), StandardCopyOption.REPLACE_EXISTING);
                                Files.delete(item.getSource());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // Clear semaphore
                            midSave = false;
                        }
                    }
                }, 0L, 1L);
            }
        }
        save(yamlFile, fileLocation, async);
    }
}