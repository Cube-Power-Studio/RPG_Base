package rpg.rpg_base.CustomMobs;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class SpawnMobs extends BukkitRunnable {
    private final RPG_Base plugin;
    private final CustomEntity customEntity = new CustomEntity();
    private final ProtectedRegion region;
    private final World world;
    private final Random random = new Random();

    public SpawnMobs(RPG_Base plugin, ProtectedRegion region, World world) {

        this.plugin = plugin;
        this.region = region;
        this.world = world;
    }


    @Override
    public void run() {
//        RPG_Base.getInstance().getLogger().info("Spawning mobs in " + region.getId());
        MobSpawnSelector mobSpawnSelector = new MobSpawnSelector();

        File mobsFolder = new File(plugin.getDataFolder() + "/custom_mobs");
        File[] mobFiles = mobsFolder.listFiles();

        if (mobFiles != null) {
            for (File mobFile : mobFiles) {
                YamlConfiguration mobConfig = YamlConfiguration.loadConfiguration(mobFile);
                Set<String> mobKeys = mobConfig.getKeys(false);

                File regionFile = new File(plugin.getDataFolder() + "/WG/mobs/" + world.getName() + "/" + region.getId() + ".yml");
                if (regionFile.exists()) {
                    YamlConfiguration regionConfig = YamlConfiguration.loadConfiguration(regionFile);
                    for (String mobKey : mobKeys) {
                        if (regionConfig.getList("mobs") != null) {
                            if (Objects.requireNonNull(regionConfig.getList("mobs")).contains(mobKey)) {
                                float chance = Float.parseFloat(Objects.requireNonNull(mobConfig.getString(mobKey + ".spawnChance")));
                                // Spawn mob based on chance and mob key
                                mobSpawnSelector.addMobChance(mobKey, chance);
                            }
                        }
                    }
                } else {
                    System.out.println("REGION DOESNT EXIST!!! : " + region.getId());
                }
            }
        } else {
            System.out.println("NO FILES PRESENT AT: " + mobsFolder.getPath());
        }


        BlockVector3 minPoint = region.getMinimumPoint();
        BlockVector3 maxPoint = region.getMaximumPoint();

        // Convert to Bukkit Locations
        Location minLocation = new Location(world, minPoint.getX(), minPoint.getY(), minPoint.getZ());
        Location maxLocation = new Location(world, maxPoint.getX(), maxPoint.getY(), maxPoint.getZ());

        Location location = new Location(world,
                (int) ((random.nextInt((int) (Math.abs(maxLocation.getBlockX() - minLocation.getX())))) + minLocation.getX()),
                (int) ((random.nextInt((int) (Math.abs(maxLocation.getBlockY() - minLocation.getY())))) + minLocation.getY()),
                (int) ((random.nextInt((int) (Math.abs(maxLocation.getBlockZ() - minLocation.getZ())))) + minLocation.getZ()));

//        int yRandomOffset = (int) ((random.nextInt((int) (Math.abs(maxLocation.getBlockY() - minLocation.getY())))) + minLocation.getY());
//        int yMin = (int) minLocation.getY();
//        int yMax = maxLocation.getBlockY();
//        System.out.println("Y Random Offset: " + yRandomOffset);
//        System.out.println("minLocation.getY(): " + yMin);
//        System.out.println("maxLocation.getBlockY(): " + yMax);


            while(!world.getBlockAt(location.getBlockX(), location.getBlockY()-1, location.getBlockZ()).getType().isSolid() ||
                    world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).getType().isSolid() ||
                    world.getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()).getType().isSolid() ||
                    world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).isLiquid() ||
                    world.getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()).isLiquid()
            ){
                location = new Location(world,
                        (int) ((random.nextInt((int) (Math.abs(maxLocation.getBlockX() - minLocation.getX())))) + minLocation.getX()),
                        (int) ((random.nextInt((int) (Math.abs(maxLocation.getBlockY() - minLocation.getY())))) + minLocation.getY()),
                        (int) ((random.nextInt((int) (Math.abs(maxLocation.getBlockZ() - minLocation.getZ())))) + minLocation.getZ()));
            }
        customEntity.spawnEntity(location, new File(plugin.getDataFolder() + "/custom_mobs/" + mobSpawnSelector.selectMob() + ".yml"), region);
    }
}
