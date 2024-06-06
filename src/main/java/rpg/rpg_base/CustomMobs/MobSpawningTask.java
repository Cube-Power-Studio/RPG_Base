package rpg.rpg_base.CustomMobs;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class MobSpawningTask extends BukkitRunnable {
    private final RPG_Base rpg_base;
    private final Set<String> scheduledRegions = new HashSet<>(); // Set to track scheduled regions

    public MobSpawningTask(RPG_Base rpg_base) {
        this.rpg_base = rpg_base;
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(weWorld);

            if (regionManager != null) {
                for (ProtectedRegion region : regionManager.getRegions().values()) {
                    if(region.getFlag(MobFlags.customMobsFlag) == StateFlag.State.ALLOW) {
                        String regionId = region.getId();
                        if (regionId.equals("__global__")) {
//                            System.out.println("Skipping __global__ region.");
                            continue; // Skip __global__ region
                        }

                        File path = new File(RPG_Base.getInstance().getDataFolder(), "/WG" + File.separator + "mobs" + File.separator + world.getName() + File.separator + regionId + ".yml");
                        if (!path.exists()) {
                            System.out.println("Region file does not exist: " + path.getAbsolutePath());
                            continue;
                        }

                        YamlConfiguration regionFile = YamlConfiguration.loadConfiguration(path);
                        int numberOfMobsInRegion = regionFile.getInt("numberOfMobsInRegion");
                        int currentMobsInRegion = MobUtil.getEntitiesWithTag(regionId, world);


                        if (!scheduledRegions.contains(regionId)) {
                            if (currentMobsInRegion <= numberOfMobsInRegion) {
                                scheduledRegions.add(regionId); // Mark region as scheduled
                                for(int i = 0; i < numberOfMobsInRegion - currentMobsInRegion; i++) {
                                    new SpawnMobs(rpg_base, region, world).runTask(RPG_Base.getInstance());
                                    System.out.println("Task scheduled for region: " + regionId);
                                }
                            }
                        } else {
                            if (currentMobsInRegion <= numberOfMobsInRegion) {
                                for(int i = 0; i < numberOfMobsInRegion - currentMobsInRegion; i++) {
                                    new SpawnMobs(rpg_base, region, world).runTask(RPG_Base.getInstance());
                                }
                                System.out.println("Region is already scheduled!: " + regionId);
                            } else {
                                scheduledRegions.remove(regionId); // Remove region from scheduled if conditions not met
                                System.out.println("Region " + regionId + " has enough mobs.");
                            }
                        }
                    }
                }
            } else {
                System.out.println("REGION MANAGER IS NOT INITIALIZED");
            }
        }
    }
}