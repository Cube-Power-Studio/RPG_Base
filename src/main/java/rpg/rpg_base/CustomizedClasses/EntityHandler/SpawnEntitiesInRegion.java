package rpg.rpg_base.CustomizedClasses.EntityHandler;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.Utils.Util;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.*;

public class SpawnEntitiesInRegion extends BukkitRunnable {
    private final RPG_Base plugin = RPG_Base.getInstance();
    private final Util util;
    private final ProtectedRegion region;
    private final World world;
    private final Random random = new Random();

    public SpawnEntitiesInRegion(Util util, ProtectedRegion region, World world) {
        this.util = util;
        this.region = region;
        this.world = world;
    }

    @Override
    public void run() {
        MobSpawnSelector mobSpawnSelector = new MobSpawnSelector();

        File mobsFolder = new File(plugin.getDataFolder() + "/custom_mobs");
        File[] mobFiles = mobsFolder.listFiles();


        if (mobFiles != null) {
            for (File mobFile : mobFiles) {
                YamlConfiguration mobConfig = YamlConfiguration.loadConfiguration(mobFile);
                Set<String> mobKeys = mobConfig.getKeys(false); // Get all mob keys from the mob config

                File regionFile = new File(plugin.getDataFolder() + "/WG/mobs/" + world.getName() + "/" + region.getId() + ".yml");
                if (regionFile.exists()) {
                    YamlConfiguration regionConfig = YamlConfiguration.loadConfiguration(regionFile);
                    List<String> regionMobList = regionConfig.getStringList("mobs"); // List of mobs in the region

                    for (String mobKey : mobKeys) {
                        if (regionMobList.contains(mobKey)) {
                            float chance;
                            // Safely get the spawn chance for the mob (use a default if not found)
                            try {
                                chance = Float.parseFloat(Objects.requireNonNull(mobConfig.getString(mobKey + ".spawnChance", "0")));
                            } catch (NumberFormatException e) {
                                chance = 0; // Default to 0 if the spawnChance is invalid
                                plugin.getLogger().warning("Invalid spawn chance for mob " + mobKey);
                            }

                            mobSpawnSelector.addMobChance(mobKey, chance); // Add mob spawn chance
                        }
                    }
                } else {
                    plugin.getLogger().warning("Region file for " + region.getId() + " does not exist!");
                }
            }

        } else {
            System.out.println("NO FILES PRESENT AT: " + mobsFolder.getPath());
        }


        BlockVector3 minPoint = region.getMinimumPoint();
        BlockVector3 maxPoint = region.getMaximumPoint();

        Location minLocation = new Location(world, minPoint.getX(), minPoint.getY(), minPoint.getZ());
        Location maxLocation = new Location(world, maxPoint.getX(), maxPoint.getY(), maxPoint.getZ());

        int minX = minLocation.getBlockX();
        int maxX = maxLocation.getBlockX();
        int minY = minLocation.getBlockY();
        int maxY = maxLocation.getBlockY();
        int minZ = minLocation.getBlockZ();
        int maxZ = maxLocation.getBlockZ();

        Location location = new Location(world,
                (random.nextInt(maxX - minX) + minX + 0.5),
                (random.nextInt(maxY - minY) + minY),
                (random.nextInt(maxZ - minZ) + minZ + 0.5));

        double x = location.getBlockX();
        double y = location.getBlockY();
        double z = location.getBlockZ();


        Block block = location.getWorld().getBlockAt((int) x, (int) y, (int) z);
        Block blockBelow = location.getWorld().getBlockAt((int) x, (int) (y - 1), (int) z);
        Block blockAbove = location.getWorld().getBlockAt((int) x, (int) (y + 1), (int) z);

        int tries = 0;

        while (!blockBelow.isSolid() ||
                blockBelow.isLiquid() ||
                block.isSolid() ||
                block.isLiquid() ||
                blockAbove.isSolid() ||
                blockAbove.isLiquid() ||
                !location.getChunk().isLoaded()
        ) {
            if (tries < 1000) {
                x = random.nextInt(maxX - minX) + minX + 0.5;
                y = random.nextInt(maxY - minY) + minY;
                z = random.nextInt(maxZ - minZ) + minZ + 0.5;

                location = new Location(world, x, y, z);

                block = location.getWorld().getBlockAt((int) x, (int) y, (int) z);
                blockBelow = location.getWorld().getBlockAt((int) x, (int) (y - 1), (int) z);
                blockAbove = location.getWorld().getBlockAt((int) x, (int) (y + 1), (int) z);

                tries++;
            } else {
                this.cancel();
                EntitySpawner.scheduledSpawns.put(region, EntitySpawner.scheduledSpawns.getOrDefault(region, 1) - 1);
                break;
            }
        }

        location.setX(location.getX());
        location.setZ(location.getZ());
        EntitySpawner.scheduledSpawns.put(region, EntitySpawner.scheduledSpawns.getOrDefault(region, 1) - 1);
        MobManager.spawnMob(mobSpawnSelector.selectMob(), location, region);
    }
}
